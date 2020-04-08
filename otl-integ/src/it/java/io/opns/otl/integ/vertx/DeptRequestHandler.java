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

import io.opns.otl.integ.model.Department;
import io.opns.otl.vertx.TracingVtxHandler;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class DeptRequestHandler implements Handler<RoutingContext> {

    private static final Logger logger = LoggerFactory.getLogger(DeptRequestHandler.class);
    
    private final Vertx vertx;
    private final JsonObject config;
    
    private final Tracer tracer = GlobalTracer.get();
    
    public DeptRequestHandler(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        this.config = config;
    }

    @Override
    public void handle(RoutingContext ctx) {
        //JsonObject deptReq = ctx.getBodyAsJson();
        Department dept = Json.decodeValue(ctx.getBody(), Department.class);
        String address = config.getString("send.address");
        
        if (logger.isInfoEnabled()) {
            logger.info("Department::RequestHandler -> Received request: " + dept);
        }
        String error = ctx.request().getParam("error");
        if (error != null && error.equals("true")) {
            throw new RuntimeException("Purposely throwing exception");
        }
        Span span = TracingVtxHandler.activeSpan(ctx);
        if (span != null) {
            try (Scope scope = tracer.activateSpan(span)) { 
                vertx.eventBus().request(address
                        , dept
                        , new DeliveryOptions().addHeader("sample.key", "sample.value")
                        , new MessageReplyHandler(ctx));

                if (logger.isInfoEnabled()) {
                    logger.info("Sent Department event to event bus. Address: " + address);
                }
            }
        }
    }
}
