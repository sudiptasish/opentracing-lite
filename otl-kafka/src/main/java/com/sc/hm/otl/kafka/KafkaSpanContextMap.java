/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.kafka;

import io.opentracing.propagation.TextMapExtract;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

/**
 * Span context map, where the contextual information will be kept after extracing
 * the same from a kafka message.
 *
 * @author Sudiptasish Chanda
 */
public class KafkaSpanContextMap implements TextMapExtract {
    
    private final Map<String, String> headers = new HashMap<>();
    
    KafkaSpanContextMap(Headers headers) {
        for (Iterator<Header> itr = headers.iterator(); itr.hasNext(); ) {
            Header header = itr.next();
            this.headers.put(header.key(), new String(header.value()));
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.entrySet().iterator();
    }
    
}
