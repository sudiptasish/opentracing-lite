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
package io.opns.otl.integ.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class MessageReplyHandler implements Handler<AsyncResult<Message<JsonObject>>> {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageReplyHandler.class);
    
    private RoutingContext ctx;
    
    public MessageReplyHandler(RoutingContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handle(AsyncResult<Message<JsonObject>> result) {
        if (result.succeeded()) {
            Message<JsonObject> message = result.result();
            JsonObject json = message.body();
            
            if (logger.isInfoEnabled()) {
                logger.info("Reply Handler received message from event bus."
                        + " Address: {}. Content: {}", message.address(), json);
            }
            if (logger.isInfoEnabled()) {
                logger.info("MessageReplyHandler -> Processing payment is successful."
                        + " Tracking Id: {}", json.getString("trackingId"));
            }
            String msg = Json.encode(json);
            ctx.response()
                .putHeader("Content-Length", String.valueOf(msg.length()))
                .setChunked(Boolean.FALSE)
                .setStatusCode(200)
                .end(msg);
        }
        else {
            logger.error("Reply Handler failed to receive message. "
                    + "Error: " + result.cause().getMessage());
            
            JsonObject json = new JsonObject();
            json.put("errorMsg", result.cause().getMessage());
            String msg = Json.encode(json);
            
            ctx.response()
                .putHeader("Content-Length", String.valueOf(msg.length()))
                .setChunked(Boolean.FALSE)
                .setStatusCode(200)
                .end(msg);
        }
    }
}
