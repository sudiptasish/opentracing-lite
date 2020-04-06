/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.spring.client;

import io.opentracing.propagation.TextMapInject;
import org.springframework.http.HttpHeaders;

/**
 * An opentracing carrier to keep the contextual info to be passed as headers.
 *
 * @author Sudiptasish Chanda
 */
public class HttpHeaderAdapter implements TextMapInject {
    
    private final HttpHeaders headers;
    
    HttpHeaderAdapter(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public void put(String key, String value) {
        headers.add(key, value);
    }
    
}
