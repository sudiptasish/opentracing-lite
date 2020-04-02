/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.kafka;

import io.opentracing.propagation.TextMapInject;
import org.apache.kafka.common.header.Headers;

/**
 * A carrier for the kafka message header.
 * 
 * @author Sudiptasish Chanda
 */
public class KafkaMsgContextCarrier implements TextMapInject {
    
    private final Headers headers;
    
    KafkaMsgContextCarrier(Headers headers) {
        this.headers = headers;
    }

    @Override
    public void put(String key, String value) {
        headers.add(key, value.getBytes());
    }
    
}
