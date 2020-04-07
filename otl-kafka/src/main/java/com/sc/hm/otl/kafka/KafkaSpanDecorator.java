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
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * A decorator for decorating the outbound span.
 *
 * @author Sudiptasish Chanda
 */
public interface KafkaSpanDecorator {
    
    /**
     * This API will be called to decorate the newly created span.
     * 
     * @param record    The record being sent
     * @param span      The current span created by the producer interceptor
     */
    void onSend(ProducerRecord<Object, Object> record, Span span);
    
    /**
     * This API will be called to decorate the newly created span by the consumer.
     * 
     * @param record    The record received from the broker
     * @param span      The span created by the consumer interceptor
     */
    <K, V> void onReceive(ConsumerRecord<K, V> record, Span span);
}
