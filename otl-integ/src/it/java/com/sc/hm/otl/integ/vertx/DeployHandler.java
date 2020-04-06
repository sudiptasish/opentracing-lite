/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class DeployHandler implements Handler<AsyncResult<String>> {

    private final Logger logger = LoggerFactory.getLogger(DeployHandler.class);

    private final String task = "Deploy::Verticle";
    private Vertx vertx;

    public DeployHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(AsyncResult<String> result) {
        if (result.failed()) {
            Throwable cause = result.cause();
            if (cause instanceof VertxException) {
                VertxException ve = (VertxException)cause;
                logger.error("Vert.x Error. Task: " + this.task, ve.getCause());
            }
            else {
                logger.error("Internal Error. Task: " + this.task, cause);
            }
            // In either case, close the vertx server instance.
            handleFailure();
        }
        else {
            if (logger.isInfoEnabled()) {
                logger.info("Succeeded in executing task: {}. Result: {}"
                        , this.task
                        , result.result());
            }
        }
    }

    /**
     * In case of failure, handle as appropriate.
     */
    private void handleFailure() {
        this.vertx.close();
    }
}
