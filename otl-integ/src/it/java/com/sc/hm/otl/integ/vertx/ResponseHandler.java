/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ResponseHandler implements Handler<AsyncResult<HttpResponse<Buffer>>> {
    
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    
    private final RoutingContext routingCtx;
    
    public ResponseHandler(RoutingContext routingCtx) {
        this.routingCtx = routingCtx;
    }

    @Override
    public void handle(AsyncResult<HttpResponse<Buffer>> result) {
        if (result.succeeded()) {
            try {
                HttpResponse<Buffer> response = result.result();
                JsonObject json = response.bodyAsJsonObject();

                if (logger.isInfoEnabled()) {
                    logger.info("Received response from remote Service. Code: {}. Result: {}"
                            , response.statusCode()
                            , json);
                }
                String msg = json.encodePrettily();
                this.routingCtx.response()
                        .putHeader("Content-Length", String.valueOf(msg.length()))
                        .setChunked(Boolean.FALSE)
                        .setStatusCode(201)
                        .end(msg);
            }
            catch (RuntimeException e) {
                logger.error("Error processing response received from remote service."
                        + " Msg: " + e.getMessage(), e);
                
                this.routingCtx.fail(400, e);
            }
        }
        else {
            logger.error("Error making call to remote Service.");
            this.routingCtx.fail(500, result.cause());
        }
    }
}
