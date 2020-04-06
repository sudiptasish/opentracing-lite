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
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.RequestCtxDecorator;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
public class RoutingEndHandler implements Handler<Void> {
    
    private static final Logger logger = LoggerFactory.getLogger(RoutingEndHandler.class);
    
    private final RoutingContext routingCtx;
    
    private final List<RequestCtxDecorator> decorators = new ArrayList<>(1);

    RoutingEndHandler(RoutingContext routingCtx) {
        this.routingCtx = routingCtx;
        decorators.add(new MiddlewareSpanDecorator());
    }

    @Override
    public void handle(Void event) {
        if (logger.isTraceEnabled()) {
            logger.trace("RoutingEndHandler is invoked.");
        }
        TracingHelper.windUp(routingCtx, decorators);
    }    
}
