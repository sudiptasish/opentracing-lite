/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import io.opentracing.Span;

/**
 * Span decorator used in a messaging context to decorate a span.
 * 
 * <p>
 * Decorating a span implies adding additional info about the span. Eventually 
 * they get stored as a tag.
 * 
 * Note that it is not mandatory for all middleware or interceptor to have a span
 * decorator. Whenever someone likes to add extra details before pasing the span over
 * to next component, a decorator can be used. The message context decorator is
 * used whenever a message producer is passing the span to the consumer. It is
 * expected that the producer or any outbound communication interceptor will use
 * this decorator to inject additional information about the span just before
 * sending it to consumer.
 * 
 * <p>
 * There is no request-response scenario for messaging. Hence it is expected that
 * every interceptor or middleware will receive some context data or header object
 * from the upstream component, from where the required information can be extracted
 * in order to enrich the span.
 *
 * @author Sudiptasish Chanda
 */
public interface MessageCtxDecorator<R> {
    
    /**
     * This method should be called at the message producer/sender side before
     * sending the event/message.
     * 
     * The queue where the message would be sent can either be in-memory or distributed
     * persistent queue. In either case, the span must be decorated before sending
     * it to the queue.
     * 
     * @param context
     * @param span      The span whose contextual information is to be sent to
     *                  the queue. The context data will be part of the message header.
     *                  
     */
    void onSend(R context, Span span);
    
    /**
     * This method should be called from the consumer side.
     * 
     * <p>
     * If there is a provision for interceptor, typically you would write an 
     * interceptor at the consumer side, which would be invoked before the message
     * is handed over to the actual consumer. It is the job of the interceptor
     * to enrich the span before the handover.
     * 
     * Like {@link #onSend} method, it is expected that the underlying messaginng
     * infrastructure will pass on some context data in order to enrich the span.
     * 
     * @param context
     * @param span      The span that is expected to be created by the interceptor
     *                  after extracing the contextual information from the message
     *                  header.
     */
    void onReceive(R context, Span span);
    
}
