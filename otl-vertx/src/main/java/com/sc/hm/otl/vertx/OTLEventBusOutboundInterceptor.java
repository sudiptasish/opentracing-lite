/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import io.opentracing.Scope;
import io.opentracing.Span;
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
 * Vertx event bus outbound interceptor.
 * 
 * <p>
 * This interceptor will be called whenever a producer sends a message to the bus.
 * The event bus can be replicated. But most of the time, people use in memory
 * event bus to communicate between the verticles. Messages sent to an in-memory
 * event bu is not persistent. You can think like event bus as an in-memory queue,
 * whenever the messages are published on the event bus, then will eventually get
 * accumulated. This interceptor will enrich the message header by adding the
 * span context data.
 * Note that, there must be an active span present in the current thread's context
 * in order for the event bus to successfully inject the span headers. Now because
 * vert.x is async, thus no active scope would ever be present inside a thread local, 
 * therefore it's the responsibility of the developer to explicitly activate the
 * current span just before sending the message.
 * 
 * <p>
 * See the below example as to how to activate a span before sending message to
 * event bus:
 * 
 * <pre>
 * {@code
 *  
 *  ....
 * 
 *  @Override
 *  public void handle(RoutingContext routingCtx) {
 *      Object message = ...
 *      String address = ...;
 *      
 *      Span span = OTLVertxHandler.currentSpan(routingCtx);
 *      if (span != null) {
 *          try (Scope scope = tracer.activateSpan(span)) {
 *              vertx.eventBus().send(address, message);
 *          }
 *      }
 *      else {
 *          vertx.eventBus().send(address, message);
 *      }
 *  }
 * 
 * }
 * </pre>
 * 
 * By default, event bus does not create a new span, because 90% of the cases the
 * eventbus is in memory, and data transfer is pretty fast, therefore it does not
 * make sense to create a span every time a message is passed through it. Otherwise
 * system would be flooded with span. For local verticles, the idea is to pass on the
 * span context as part of the message, so that the receiving verticle or the
 * consumer can have the context for it's reference. The receiving verticle on the
 * other side may decide to create it's own span to represent it's own work. In
 * which case, the consumer must take care of closing the span as appropriate.
 * 
 * Note: For distributed event bus, span creation during message delivery can be
 *       enabled by setting the system property <code>-Dvertx.eventbus.span<code>
 *       to true.
 *
 * @author Sudiptasish Chanda
 */
public class OTLEventBusOutboundInterceptor<T> implements Handler<DeliveryContext<T>> {
    
    private final Logger logger = LoggerFactory.getLogger(OTLEventBusOutboundInterceptor.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    private final List<VertxEventBusSpanDecorator> decorators = new ArrayList<>();
    
    OTLEventBusOutboundInterceptor() {
        decorators.add(new StandardVertxEventBusSpanDecorator());
    }

    @Override
    public void handle(DeliveryContext<T> deliveryCtx) {
        boolean enableSpan = Boolean.parseBoolean(System.getProperty("vertx.eventbus.span", "false"));
        
        if (logger.isTraceEnabled()) {
            logger.trace("Value of vertx.eventbus.span is: " + enableSpan);
        }
        if (enableSpan) {
            // The currently active span would automatically become the parent 
            // span of this newly created span.
            Span span = tracer.buildSpan("vertx-send").start();

            try (Scope scope = tracer.activateSpan(span)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Created new eventbus sender span: {}", span.context());
                }
                
                tracer.inject(span.context()
                    , Format.Builtin.TEXT_MAP_INJECT
                    , new VertxDeliveryContextAdapter(deliveryCtx.message().headers()));

                for (VertxEventBusSpanDecorator decorator : decorators) {
                    decorator.onSend(deliveryCtx, span);
                }
            }
            finally {
                span.finish();
            }
            // After the call, the old span becomes active again.
        }
        else {
            // Span creation is disabled. Therefore just extract the span context
            // from the currently active span and propagate.
            // If no active span, then nothing will be propagated.
            // Note that, this behavior can be confusing, because the consumer, at
            // the other side will receive the contextual info, and it may treat
            // that as a followup span created by sender. Therefore it is
            // essential to inject a special header "no.span=true" in order for
            // the consumer to distinguish it easily.
            
            // The flag, -Dvertx.eventbus.span should be disabled only if user 
            // experiences a sudden influx of span.
            Span span = tracer.activeSpan();
            
            if (span != null) {
                tracer.inject(span.context()
                    , Format.Builtin.TEXT_MAP_INJECT
                    , new VertxDeliveryContextAdapter(deliveryCtx.message().headers()));
                
                if (logger.isTraceEnabled()) {
                    logger.trace("Span creation is disabled in vertx eventbus sender."
                        + " Therefore propagating the current contextual info from the"
                        + " span: {}", span.context().toSpanId());
                }
            }
            else {
                if (logger.isTraceEnabled()) {
                    logger.trace("No Active span exist. Context won't be propagated");
                }
            }
        }       
        // Call the next middleware (if any)
        deliveryCtx.next();
    }
    
}
