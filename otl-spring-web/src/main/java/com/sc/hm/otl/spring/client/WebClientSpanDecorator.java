/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.spring.client;

import io.opentracing.Span;
import org.springframework.web.reactive.function.client.ClientRequest;

/**
 * Decorator responsible for decorating a span while making a client call using
 * spring react web client.
 * 
 * Currently {@link WebClientInterceptor} does not support creating a new span at
 * the time of making outbound call. It's job is to propagate the contextual info
 * to the next service. Therefore the decorator is InActive today.
 *
 * @author Sudiptasish Chanda
 */
public interface WebClientSpanDecorator {
    
    /**
     * 
     * @param request
     * @param span 
     */
    void onRequest(ClientRequest request, Span span);
}
