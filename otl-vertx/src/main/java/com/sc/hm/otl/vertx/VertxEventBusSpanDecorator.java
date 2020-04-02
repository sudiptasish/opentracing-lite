/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import io.opentracing.Span;
import io.vertx.core.eventbus.DeliveryContext;

/**
 *
 * @author Sudiptasish Chanda
 */
public interface VertxEventBusSpanDecorator {
    
    /**
     * This API will be called to decorate the newly created span.
     * 
     * @param deliveryCtx
     * @param span 
     */
    <T> void onSend(DeliveryContext<T> deliveryCtx, Span span);
    
    /**
     * This API will be called by the inbound interceptor to decorate the span.
     * 
     * @param deliveryCtx
     * @param span 
     */
    <T> void onReceive(DeliveryContext<T> deliveryCtx, Span span);
}
