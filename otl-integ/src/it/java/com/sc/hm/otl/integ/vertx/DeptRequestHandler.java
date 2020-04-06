/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.vertx;

import com.sc.hm.otl.integ.model.Department;
import com.sc.hm.otl.vertx.TracingVtxHandler;
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
