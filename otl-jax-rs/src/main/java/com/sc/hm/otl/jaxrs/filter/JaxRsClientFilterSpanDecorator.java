/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import io.opentracing.Span;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;

/**
 * Interface that represents a span decorator for jax rs client.
 * 
 * This decorator will be called while propagating the span context to the next
 * service in the chain.
 *
 * @author Sudiptasish Chanda
 */
public interface JaxRsClientFilterSpanDecorator {
    
    /**
     * Decorate this span by taking the appropriate header values from the request context.
     * This method will be invoked just before calling the downstream service.
     * 
     * @param requestContext
     * @param span 
     */
    void onRequest(ClientRequestContext requestContext, Span span);
    
    /**
     * Decorate the span just after receiving the response from the downstream.
     * 
     * @param requestContext
     * @param responseContext
     * @param span 
     */
    void onResponse(ClientRequestContext requestContext
        , ClientResponseContext responseContext
        , Span span);
}
