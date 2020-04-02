/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
     * @param record
     * @param span 
     */
    void onSend(ProducerRecord<Object, Object> record, Span span);
    
    /**
     * This API will be called to decorate the newly created span by the consumer.
     * 
     * @param record
     * @param span 
     */
    <K, V> void onReceive(ConsumerRecord<K, V> record, Span span);
}
