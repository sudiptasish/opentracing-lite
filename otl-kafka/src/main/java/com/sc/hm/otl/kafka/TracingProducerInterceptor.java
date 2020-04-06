/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.kafka;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The interceptor class for kafka producer.
 * 
 * <p>
 * An interceptor is a kind of plugin/hook that allows you to intercept the records
 * received by the producer before they are actually published to the Kafka cluster.
 * The idea is to capture the record(s) to be sent to remote broker and inject the
 * opentracing headers.
 * 
 * <p>
 * This is how you can use the interceptor.
 * <pre>
 * {@code
     .....
     public Producer createProducer() {
         Producer<String, T> producer;
         
         Properties config = new Properties();
         config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
         config.put(ProducerConfig.CLIENT_ID_CONFIG, "Producer-1");
         ....
         ....
         config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "com.sc.hm.otl.kafka.TracingProducerInterceptor");
 
         producer = new KafkaProducer<String, T>(config);
         return producer
     }
 
     public void send(String topic, T msg) {
         Producer<String, T> producer = createProducer();
         ProducerRecord<String, T> record = new ProducerRecord<>(topic, msg);
         producer.send(record);
         
         producer.close();
     }
 }
 * </pre>
 * 
 * <p>
 * Note that the interceptor will always create a new span whether a parent span exist or not.
 * If no active span exists, then no contextual info will be sent to broker.
 * The idea of creating a new span is to represent the duration in time the message
 * waited at the queue to be consumed later. The consumer interceptor on the other
 * side, therefore must create a new follows_from span immediately after polling the
 * message and close it.
 * So effectively for same message, we would have two different spans created,
 * and the relationship would FOLLOWS_FROM as opposed to CHILD_OF.
 * 
 * You can, however, disable this feature by setting the system property 
 * <pre>-Dkafka.producer.span<pre> to false. In which case, it will check if any
 * span exists in the current thread context, if so, then helps propagate the span
 * context to the broker. Such that any consumer running other side would eventually
 * receive the same span.
 * 
 * <p>
 * Note that, prior 0.8.0 there was no concept of a message header. So if you are
 * on 0.8.0, upgrade you kafka client to the latest version.
 *
 * @author Sudiptasish Chanda
 */
public class TracingProducerInterceptor implements ProducerInterceptor<Object, Object> {

    private final Logger logger = LoggerFactory.getLogger(TracingProducerInterceptor.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    private final List<KafkaSpanDecorator> decorators = new ArrayList<>();
    
    public TracingProducerInterceptor() {
        decorators.add(new StandardKafkaSpanDecorator());
    }

    @Override
    public void configure(Map<String, ?> config) {
        // Check if any custom decorator is provided.
        // If so, add it to the list.
        KafkaSpanDecorator customDecorator = 
            (KafkaSpanDecorator)config.get("kafka.producer.decorator");
        
        if (customDecorator != null) {
            decorators.add(customDecorator);
        }
    }
    
    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record) {
        // This methid will be called from KafkaProducer.send(ProducerRecord) 
        // or KafkaProducer.send(ProducerRecord, Callback) methods, before key and value
        // get serialized and partition is assigned (if partition is not specified in ProducerRecord).
        
        // The ProducerRecord object has the record header.
        Boolean enableSpan = Boolean.parseBoolean(System.getProperty("kafka.producer.span", "true"));
        
        if (logger.isTraceEnabled()) {
            logger.trace("Value of kafka.producer.span is: " + enableSpan);
        }
        if (enableSpan) {
            // The currently active span would automatically become the parent 
            // span of this newly created span.
            Span span = tracer.buildSpan("kafka-send").start();

            try (Scope scope = tracer.activateSpan(span)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Created new kafka producer span: {}", span.context());
                }

                // Kafka producer interceptor is a little different than any standard
                // synchronoous interceptor, where both request and response is handled
                // by the same interceptor API.
                // Whatever span is created by the interceptor, will be immediately 
                // closed by the interceptor itself. The span will just indicate that
                // a message has been sent to remote kafka broker.

                tracer.inject(span.context()
                    , Format.Builtin.TEXT_MAP_INJECT
                    , new KafkaMsgContextCarrier(record.headers()));

                for (KafkaSpanDecorator decorator : decorators) {
                    decorator.onSend(record, span);
                }
            }
            finally {
                span.finish();
            }
            // After the call, the old span becomes active again.
        }
        else {
            // Span creation is disabled. Therefore just extract the span context
            // from the currently active span and propagate.
            // If no active span, then nothing will be propagated.
            // Note that, this behavior can be confusing, because the consumer, at
            // the other side will receive the contextual info, and it may treat
            // that as a followup span created by producer. Therefore it is
            // essential to inject a special header "no.span=true" in order for
            // the consumer to distinguish it easily.
            
            // The flag, -Dkafka.producer.span should be disabled only if user 
            // experiences a sudden influx of span.
            Span span = tracer.activeSpan();
            
            if (span != null) {
                tracer.inject(span.context()
                    , Format.Builtin.TEXT_MAP_INJECT
                    , new KafkaMsgContextCarrier(record.headers()));
                
                if (logger.isTraceEnabled()) {
                    logger.trace("Span creation is disabled in kafka producer. Therefore propagating the"
                        + " current contextual info from the span: {}", span.context().toSpanId());
                }
            }
            else {
                if (logger.isTraceEnabled()) {
                    logger.trace("No Active span exist. Context won't be propagated");
                }
            }
        }
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata rm, Exception excptn) {
        // This method is called when the record sent to the server has been acknowledged,
        // or when sending the record fails before it gets sent to the server.
        // It does not make sense to handle any span on acknowledgement, as that
        // defies the whole purpose of kafka's asynchronous behavior.
    }

    @Override
    public void close() {
        // Do Nothing
    }
    
}
