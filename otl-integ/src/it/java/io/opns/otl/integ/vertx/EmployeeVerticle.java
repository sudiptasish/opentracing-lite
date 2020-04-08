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

import io.opns.otl.vertx.TracingVtxErrorHandler;
import io.opns.otl.vertx.TracingVtxHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class EmployeeVerticle extends AbstractVerticle {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeVerticle.class);
    
    private static final int DEFAULT_PORT = 8080;
    private static final String URI = "/emp/api/v1/employees";
    
    private HttpServer httpServer;

    @Override
    public void start() throws Exception {
        JsonObject config = config().getJsonObject("http.server");
        
        HttpServerOptions options = new HttpServerOptions()
                .setLogActivity(config.getBoolean("log.activity", Boolean.TRUE))
                .setSsl(config.getBoolean("ssl.enabled", Boolean.FALSE))
                .setCompressionSupported(config.getBoolean("compression.enabled", Boolean.TRUE));
        
        httpServer = getVertx().createHttpServer(options);
        
        Router router = Router.router(getVertx());
        
        // Add the OTL router hook
        // -1 indicates that this should be the first middleware to be invoked,
        // thereby acting as a web filter.
        router.route().order(-1)
            .handler(new TracingVtxHandler())
            .failureHandler(new TracingVtxErrorHandler());
        
        router.route().handler(BodyHandler.create());
        router.route().handler(LoggerHandler.create(LoggerFormat.DEFAULT));
        router.route(URI)
                .handler(new EmployeeRequestHandler(getVertx(), config()))
                .failureHandler(new ErrorHandler());
        
        httpServer.requestHandler(router);

        // Once the HTTP server is created, one can start it
        // using its listen() method.
        httpServer.listen(config.getInteger("http.port", DEFAULT_PORT));
        
        if (logger.isInfoEnabled()) {
            logger.info("Started Employee Http Server. Listening to port: {}", httpServer.actualPort());
        }
    }
}
