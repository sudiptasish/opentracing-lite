/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.MessageCtxDecorator;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import io.vertx.core.eventbus.DeliveryContext;

/**
 * A vertx eventbus span decorator.
 * 
 * A vertx eventbus can be either in memory or a distributed one. When the sender
 * is instantiates a ne span to represent a send task, it will use this decorator
 * to enrich the newly created span by taking certain information from the 
 * delivery context object.
 *
 * @author Sudiptasish Chanda
 */
public class EventbusSpanDecorator implements MessageCtxDecorator<DeliveryContext<?>> {
    
    public static final String SENDER = "eventbus.send";
    public static final String RECEIVER = "eventbus.receive";

    @Override
    public void onSend(DeliveryContext<?> context, Span span) {
        Tags.COMPONENT.set(span, SENDER);
        Tags.MESSAGE_BUS_DESTINATION.set(span, context.message().address());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_PRODUCER);
    }

    @Override
    public void onReceive(DeliveryContext<?> context, Span span) {
        // TODO
    }
    
}
