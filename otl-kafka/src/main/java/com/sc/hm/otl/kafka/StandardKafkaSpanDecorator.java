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
package com.sc.hm.otl.kafka;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 *
 * @author Sudiptasish Chanda
 */
public class StandardKafkaSpanDecorator implements KafkaSpanDecorator {
    
    public static final String COMPONENT_KAFKA_PRODUCER = "kafka.producer";
    public static final String COMPONENT_KAFKA_CONSUMER = "kafka.consumer";
    
    @Override
    public void onSend(ProducerRecord<Object, Object> record, Span span) {
        Tags.COMPONENT.set(span, COMPONENT_KAFKA_PRODUCER);
        Tags.MESSAGE_BUS_DESTINATION.set(span, record.topic());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_PRODUCER);
        
        // Set the partition, only if it is a low level kafka producer.
        if (record.partition() != null) {
            span.setTag("partition", record.partition());
        }
    }
    
    @Override
    public <K, V> void onReceive(ConsumerRecord<K, V> record, Span span) {
        Tags.COMPONENT.set(span, COMPONENT_KAFKA_CONSUMER);
        Tags.MESSAGE_BUS_DESTINATION.set(span, record.topic());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CONSUMER);
        
        span.setTag("offset", record.offset());
        span.setTag("partition", record.partition());
    }
    
}
