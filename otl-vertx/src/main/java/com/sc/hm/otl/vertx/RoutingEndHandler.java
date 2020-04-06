/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.RequestCtxDecorator;
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
public class RoutingEndHandler implements Handler<Void> {
    
    private static final Logger logger = LoggerFactory.getLogger(RoutingEndHandler.class);
    
    private final RoutingContext routingCtx;
    
    private final List<RequestCtxDecorator> decorators = new ArrayList<>(1);

    RoutingEndHandler(RoutingContext routingCtx) {
        this.routingCtx = routingCtx;
        decorators.add(new MiddlewareSpanDecorator());
    }

    @Override
    public void handle(Void event) {
        if (logger.isTraceEnabled()) {
            logger.trace("RoutingEndHandler is invoked.");
        }
        TracingHelper.windUp(routingCtx, decorators);
    }    
}
