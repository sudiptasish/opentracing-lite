/*
 *     Copyright 2020 Opentracing-LiTE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.opns.otl.kafka;

import io.opentracing.References;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The interceptor class for kafka consumer.
 * 
 * <p>
 * A plugin interface that allows you to intercept the records received by the consumer.
 * Span propagation over message bus is always async, where the sender/producer
 * takes the responsibility of creating a new span and passes on the context data,
 * which will eventually be acknowledged by the consumer, as it creates a follows_from
 * span and finished it immediately. Thus the two spans in question represents the amount
 * of time a kafka message actually waited in the queue until before it's consumption.
 * 
 * <p>
 * This is how you can use the interceptor.
 * <pre>
 * {@code
     .....
     public Consumer createConsumer() {
         Consumer<String, byte[]> consumer;
         
         Properties config = new Properties();
         
         config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
         config.put(ConsumerConfig.CLIENT_ID_CONFIG, "Consumer-1");
         ....
         ....
         config.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG
                 , "io.opns.otl.kafka.TracingConsumerInterceptor");
 
         consumer = new KafkaConsumer<String, byte[]>(config);
         return consumer
     }
 
     public void receive(String topic) {
         Consumer<String, byte[]> consumer = createConsumer();
         consumer.subscribe(Collections.singletonList(topic));
 
         while (true) {
             ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000L));
             if (!records.isEmpty()) {
                  for (ConsumerRecord<String, byte[]> record : records) {
                      // process the individual message
                  }
             }
             consumer.commitSync();
         }
         // consumer.close();
     }
 }
 * </pre>
 *
 * <p>
 * Note that the interceptor will not create any new span. It will acknowledge the
 * span present in individual message, and alose it accordingly, just before 
 * consuming the same, thus marking the end of that span.
 * The application code (followup from consumer) may choose to create a new span
 * to represent it's own work.
 * 
 * @author Sudiptasish Chanda
 */
public class TracingConsumerInterceptor<K, V> implements ConsumerInterceptor<K, V> {
    
    private final Logger logger = LoggerFactory.getLogger(TracingConsumerInterceptor.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    private final List<KafkaSpanDecorator> decorators = new ArrayList<>();

    public TracingConsumerInterceptor() {
        decorators.add(new StandardKafkaSpanDecorator());
    }

    @Override
    public void configure(Map<String, ?> map) {
        // Do Nothing.
    }

    @Override
    public ConsumerRecords<K, V> onConsume(ConsumerRecords<K, V> records) {
        // This is called just before the records are returned by KafkaConsumer.poll(...)

        // Extract the span context from the message header.
        // If no span context is present, then do nothing...
        for (ConsumerRecord<K, V> record : records) {
            SpanContext context = tracer.extract(Format.Builtin.TEXT_MAP_EXTRACT
                , new KafkaSpanContextMap(record.headers()));
            
            if (context != null) {
                // Create a follows_from span out of this context and close it immediately.
                // It is not expected for the consumer to have a span at this point.
                Span span = tracer.buildSpan("kafka-recieve")
                    .addReference(References.FOLLOWS_FROM, context)
                    .start();
                
                try (Scope scope = tracer.activateSpan(span)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Created new kafka consumer span: {}", span.context());
                    }

                    // Now, decorate the new span and close it.
                    for (KafkaSpanDecorator decorator : decorators) {
                        decorator.onReceive(record, span);
                    }
                }
                finally {
                    span.finish();
                }
            }
            else {
                if (logger.isTraceEnabled()) {
                    logger.trace("No span context present in the kafka message.");
                }
            }
        }
        return records;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {
        // Do Nothing.
    }

    @Override
    public void close() {
        // Do Nothing.
    }
    
}
