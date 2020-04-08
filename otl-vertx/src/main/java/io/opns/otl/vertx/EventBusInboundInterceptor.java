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
package io.opns.otl.vertx;

import io.opns.otl.core.MessageCtxDecorator;
import io.opentracing.References;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryContext;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vertx eventbus inbound interceptor.
 * 
 * <p>
 * Inbound interceptor is a kind of interceptor that will be called whenever a 
 * message is received by Vert.x consumer.
 * 
 * 
 *
 * @author Sudiptasish Chanda
 */
public class EventBusInboundInterceptor<T> implements Handler<DeliveryContext<T>> {
    
    private final Logger logger = LoggerFactory.getLogger(EventBusInboundInterceptor.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    private final List<MessageCtxDecorator> decorators = new ArrayList<>();
    
    EventBusInboundInterceptor() {
        decorators.add(new EventbusSpanDecorator());
    }

    @Override
    public void handle(DeliveryContext<T> deliveryCtx) {
        // This is called just before the records are consumed by the cnsumer
        
        SpanContext context = tracer.extract(Format.Builtin.TEXT_MAP_EXTRACT
                , new EventBusCtxCarrier(deliveryCtx.message().headers()));
            
        if (context != null) {
            // Create a follows_from span out of this context and close it immediately.
            // It is not expected for the consumer to have a span at this point.
            Span span = tracer.buildSpan("vertx-recieve")
                .addReference(References.FOLLOWS_FROM, context)
                .start();
            
            try (Scope scope = tracer.activateSpan(span)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Created new eventbus consumer span: {}", span.context());
                }

                for (MessageCtxDecorator decorator : decorators) {
                    decorator.onReceive(deliveryCtx, span);
                }
            }
            finally {
                span.finish();
            }
        }
        else {
            if (logger.isTraceEnabled()) {
                logger.trace("No span context present in the eventbus message.");
            }
        }     
        // Call the next middleware (if any)
        deliveryCtx.next();
    }
}
