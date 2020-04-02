/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.util.OTLConstants;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLVertxHandler implements Handler<RoutingContext> {
    
    private static final Logger logger = LoggerFactory.getLogger(OTLVertxHandler.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    private final List<VertxMiddlewareSpanDecorator> decorators = new ArrayList<>(1);
    
    public OTLVertxHandler() {
        decorators.add(new StandardVertxMiddlewareSpanDecorator());
    }

    @Override
    public void handle(RoutingContext routingCtx) {
        if (routingCtx.failed()) {
            Span span = routingCtx.get(OTLConstants.VERTX_ACTIVE_SPAN);
        
            if (logger.isTraceEnabled()) {
                logger.trace("Routing context failure. Current span: " + span);
            }
            if (span != null) {
                for (VertxMiddlewareSpanDecorator decorator : decorators) {
                    decorator.onError(routingCtx.request()
                        , routingCtx.response()
                        , routingCtx.failure()
                        , span);
                }
                span.finish();
            }
            routingCtx.next();
        }
        else {
            // Now start tracing the request.
            // Create a new span after extracting the span context from the request.
            SpanContext context = tracer.extract(Format.Builtin.TEXT_MAP_EXTRACT
                , new VertxMsgContextCarrier(routingCtx.request()));

            // Create the span.
            // Creation of span does not necessarily create the scope. Scope indicates
            // the work being done by the current thread at any given point of time.
            // And if that work is to represented by a span, then the span should be
            // explicitly set in the current scope before executing the task.
            Span span = tracer.buildSpan(routingCtx.request().method().name())
                .asChildOf(context)
                .ignoreActiveSpan()
                .start();

            if (logger.isTraceEnabled()) {
                logger.trace("OTL Vertx handler created new Span: {}", span.context());
            }

            for (VertxMiddlewareSpanDecorator decorator : decorators) {
                decorator.onRequest(routingCtx.request(), span);
            }
            // Because vertx request-response is async (event loop), therefore the span
            // cannot be kept in current threadlocal. The same thread would be handling
            // multiple client requests, thus there is a hig chance of overwriting the
            // old context with new value.
            // Also, as of today the contextual info cannot be passed from one thread to other.
            // Hence, it is best to manually propagate the context data.
            routingCtx.put(OTLConstants.VERTX_ACTIVE_SPAN, span);
            
            // A body end handler will be attached to Every request.
            // This will be invoked once the final response is sent to client.
            // Only then, should it close the span.
            // In case of an exception, the closing of the span will be taken care
            // of by the error handler.
            routingCtx.addBodyEndHandler(new OTLRoutingEndHandler(routingCtx));

            // Call the next handler (middleware) on the request chain.
            routingCtx.next();
        }
    }
    
    /**
     * Return the current span from the routing context.
     * 
     * @param routingCtx
     * @return Span
     */
    public static Span currentSpan(RoutingContext routingCtx) {
        return routingCtx.get(OTLConstants.VERTX_ACTIVE_SPAN);
    }
}
