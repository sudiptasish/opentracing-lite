/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.vertx;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ErrorHandler implements Handler<RoutingContext> {
    
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @Override
    public void handle(RoutingContext context) {
        logger.error("Error Handler Invoked.", context.failure());
        ErrorMessage errorMsg = new ErrorMessage(500, context.failure().getMessage());
        context.response()
                .setStatusCode(context.statusCode())
                .end(Json.encodePrettily(errorMsg));
    }
}
