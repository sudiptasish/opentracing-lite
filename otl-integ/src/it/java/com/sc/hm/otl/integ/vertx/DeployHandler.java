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
