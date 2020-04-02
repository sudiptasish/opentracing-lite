/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import io.vertx.core.eventbus.DeliveryContext;

/**
 *
 * @author Sudiptasish Chanda
 */
public class StandardVertxEventBusSpanDecorator implements VertxEventBusSpanDecorator {
    
    public static final String COMPONENT_EVENTBUS_SENDER = "eventbus-sender";
    public static final String COMPONENT_EVENTBUS_RECEIVER = "eventbus-receiver";

    @Override
    public <T> void onSend(DeliveryContext<T> deliveryCtx, Span span) {
        Tags.COMPONENT.set(span, COMPONENT_EVENTBUS_SENDER);
        Tags.MESSAGE_BUS_DESTINATION.set(span, deliveryCtx.message().address());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_PRODUCER);
    }

    @Override
    public <T> void onReceive(DeliveryContext<T> deliveryCtx, Span span) {
        // TODO
    }
    
}
