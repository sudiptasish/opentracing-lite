/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import io.opentracing.propagation.TextMapExtract;
import io.vertx.core.http.HttpServerRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Vertx span context carrier class that will hold all the header values.
 *
 * @author Sudiptasish Chanda
 */
public class VertxMsgContextCarrier implements TextMapExtract {
    
    private final Map<String, String> headers = new HashMap<>();
    
    VertxMsgContextCarrier(HttpServerRequest request) {
        for (Iterator<Map.Entry<String, String>> itr = request.headers().iterator(); itr.hasNext(); ) {
            Map.Entry<String, String> me = itr.next();
            headers.put(me.getKey(), me.getValue());
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.entrySet().iterator();
    }
    
}
