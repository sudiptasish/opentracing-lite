/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import io.opentracing.Span;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;

/**
 * Interface that represents a span decorator for jax rs.
 * If user decides not to use the {@OTLFilter}, then another option to trap the
 * incoming request is to use a {@link ContainerRequestFilter}. Once the request
 * is intercepted, then this decorator must be used to decorate the newly created
 * span.
 *
 * @author Sudiptasish Chanda
 */
public interface JaxRsFilterSpanDecorator {
    
    /**
     * Decorate this span by taking the appropriate header values from the request context.
     * This method will be invoked only after receiving/intercepting a new request,
     * and post creating the new span.
     * 
     * @param crc
     * @param span 
     */
    void onRequest(ContainerRequestContext crc, Span span);
    
    /**
     * Decorate the span just before the response is sent to the caller.
     * 
     * @param requestContext
     * @param responseContext
     * @param span 
     */
    void onResponse(ContainerRequestContext requestContext
        , ContainerResponseContext responseContext
        , Span span);
}
