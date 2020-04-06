/*
 *     Copyright 2020 Opentracing-LiTE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
