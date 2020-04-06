/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import io.opentracing.Span;

/**
 * Span decorator used in a request-response paradigm to decorate a span.
 * 
 * <p>
 * Decorating a span implies adding additional info about the span. Eventually 
 * they get stored as a tag.
 * 
 * Note that it is not mandatory for all middleware or interceptor to have a span
 * decorator. Whenever someone likes to add extra details before pasing the span over
 * to next component, a decorator can be used. The request context decorator is
 * used whenever a component/service is synchronously calling some other service 
 * and expecting a response. It is expected that the calling service will use
 * some interceptor which will use this decorator to inject additional information
 * about the span just before calling the second service.
 * 
 * <p>
 * For a typical request-response scenario, it is expected that the caller will
 * pass on a request and a response object to decorate the span. Typically the
 * request object will be used while making the outbound communication, and the
 * response object will be used after receiving a response from the called service.
 *
 * @author Sudiptasish Chanda
 */
public interface RequestCtxDecorator<R, S> {
    
    /**
     * This method should be called at the sender side before invoking the remote
     * service end point.
     * 
     * @param request   Platfor specific request object.
     * @param span      The span whose contextual information is to be sent to
     *                  the queue. The context data will be part of the message header.
     *                  
     */
    void onRequest(R request, Span span);
    
    /**
     * This method should be called immediately after getting a response from the
     * called service.
     * 
     * <p>
     * Like {@link #onSend} method, it is expected that the underlying messaginng
     * infrastructure will pass on some context data in order to enrich the span.
     * 
     * @param request   Platform specific request object
     * @param response  Platform specific response object.
     * @param span      The span that is expected to be created by the interceptor
     *                  after extracing the contextual information from the message
     *                  header.
     */
    void onResponse(R request, S response, Span span);
    
    /**
     * This method will be invoked, when the service has run into some issue
     * while processing a client request.
     * 
     * @param request   Platform specific request object
     * @param response  Platform specific response object.
     * @param e         The exception object
     * @param span      The span that is expected to be created by the interceptor
     *                  after extracing the contextual information from the message
     *                  header.
     */
    void onError(R request, S response , Throwable e, Span span);
    
}
