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
import io.opns.otl.integ.model.Employee;
import io.opns.otl.vertx.TracingVtxHandler;
import io.opns.otl.vertx.VertxWebClientInterceptor;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.impl.WebClientInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class EmployeeRequestHandler implements Handler<RoutingContext> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeRequestHandler.class);
    
    private final WebClient deptClient;
    
    private final String deptHost;
    private final int deptPort;
    private final String deptUri;
    
    private final Tracer tracer = GlobalTracer.get();
    
    public EmployeeRequestHandler(Vertx vertx, JsonObject config) {
        this.deptClient = WebClient.create(vertx, new WebClientOptions()
                .setSsl(false)
                .setTrustAll(true)
                .setVerifyHost(false)
                .setMaxPoolSize(5));
        
        ((WebClientInternal)deptClient).addInterceptor(new VertxWebClientInterceptor());
        
        String cmdHost = System.getProperty("dept.host");
        if (cmdHost != null) {
            logger.info("Custom host provided for department service: {}", cmdHost);
        }
        
        this.deptHost = cmdHost != null ? cmdHost : config.getString("dept.host");
        this.deptPort = config.getInteger("dept.port");
        this.deptUri = config.getString("dept.uri");
    }

    @Override
    public void handle(RoutingContext ctx) {
        //JsonObject deptReq = ctx.getBodyAsJson();
        Employee emp = Json.decodeValue(ctx.getBody(), Employee.class);
        
        Span span = TracingVtxHandler.activeSpan(ctx);
        try (Scope scope = tracer.activateSpan(span)) {
            if (logger.isInfoEnabled()) {
                logger.info("Employee::RequestHandler -> Received request: " + emp);
            }
            // Send Request to Department service.
            Department dept = emp.getDept();
            JsonObject json = new JsonObject();
            json.put("id", dept.getId());
            json.put("name", dept.getName());

            deptClient.post(deptPort, deptHost, deptUri)
                    .sendJson(json, new ResponseHandler(ctx));

            if (logger.isInfoEnabled()) {
                logger.info("Sending request to Department Service");
            }
        }
    }
}
