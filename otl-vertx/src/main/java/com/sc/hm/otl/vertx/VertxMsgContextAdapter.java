/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import io.opentracing.propagation.TextMapInject;
import io.vertx.core.MultiMap;

/**
 * Vertx delivery context adapter class to be used to inject the span context.
 *
 * @author Sudiptasish Chanda
 */
public class VertxMsgContextAdapter implements TextMapInject {
    
    private final MultiMap header;
    
    VertxMsgContextAdapter(MultiMap header) {
        this.header = header;
    }

    @Override
    public void put(String key, String value) {
        header.add(key, value);
    }
    
}
