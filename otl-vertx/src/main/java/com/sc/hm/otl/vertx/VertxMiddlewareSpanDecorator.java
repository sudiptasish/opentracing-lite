/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import io.opentracing.Span;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * Span decorator is used to decorate a span.
 * 
 * Decorating a span implies adding additional info about the span. Eventually 
 * they get stored as a tag.
 * Note that it is not mandatory to provide a span decorator. The {@link OTLVertxHandler}
 * by default includes the {@link StandardVertxMiddlewareSpanDecorator}, which takes most
 * of the responsibility of decorating the newly created span.
 *
 * @author Sudiptasish Chanda
 */
public interface VertxMiddlewareSpanDecorator {
    
    /**
     * This method will be invoked when the {@link OTLVertxHandler} has just created the
     * span. The span is usually created immediately after intercepting the request.
     * 
     * @param request
     * @param span 
     */
    void onRequest(HttpServerRequest request, Span span);
    
    /**
     * This method will be invoked, when the {@link OTLVertxHandler} is about to send
     * the final response to the client.
     * 
     * @param request
     * @param response
     * @param span 
     */
    void onResponse(HttpServerRequest request, HttpServerResponse response, Span span);
    
    /**
     * This method will be invoked, when the {@link OTLVertxHandler} runs into some issue
     * while processing a client request.
     * 
     * @param request
     * @param response
     * @param e
     * @param span 
     */
    void onError(HttpServerRequest request, HttpServerResponse response, Throwable e, Span span);
}
