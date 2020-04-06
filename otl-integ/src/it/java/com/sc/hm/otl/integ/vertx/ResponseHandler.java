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
