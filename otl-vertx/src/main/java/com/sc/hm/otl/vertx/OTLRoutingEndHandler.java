/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import io.opentracing.Span;
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
public class OTLRoutingEndHandler implements Handler<Void> {
    
    private static final Logger logger = LoggerFactory.getLogger(OTLRoutingEndHandler.class);
    
    private final RoutingContext routingCtx;
    
    private final List<VertxMiddlewareSpanDecorator> decorators = new ArrayList<>(1);

    OTLRoutingEndHandler(RoutingContext routingCtx) {
        this.routingCtx = routingCtx;
        decorators.add(new StandardVertxMiddlewareSpanDecorator());
    }

    @Override
    public void handle(Void event) {
        Span span = OTLVertxHandler.currentSpan(routingCtx);
        
        if (logger.isTraceEnabled()) {
            logger.trace("OTLRoutingEndHandler is invoked. Current span: " + span);
        }
        if (span != null) {
            for (VertxMiddlewareSpanDecorator decorator : decorators) {
                decorator.onResponse(routingCtx.request(), routingCtx.response(), span);
            }
            span.finish();
        }
    }
    
}
