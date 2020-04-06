/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import io.opentracing.propagation.TextMapExtract;
import io.vertx.core.MultiMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
public class EventBusCtxCarrier implements TextMapExtract {
 
    private final MultiMap headers;
    
    EventBusCtxCarrier(MultiMap headers) {
        this.headers = headers;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.iterator();
    }
}
