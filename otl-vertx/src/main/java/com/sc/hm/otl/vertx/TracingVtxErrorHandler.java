/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.RequestCtxDecorator;
import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A tracing error handler is added alongside {@link TracingVtxHandler}.
 * 
 * <p>
 * A {@link Router} can also have a error handler associated to it. This error
 * will be added to the main router and thus handler be called whenever any request
 * has failed.
 * Here is how you can register the erorr handler:
 * 
 * <pre>
 * {@code
 *     .....
 * 
 *     Router router = Router.router(vertx);
 *     router.route().order(-1)
 *          .handler(new TracingVtxHandler())
 *          .failureHandler(new TracingVtxErrorHandler())
 *          
 * }
 * </pre>
 *
 * @author Sudiptasish Chanda
 */
public class TracingVtxErrorHandler implements Handler<RoutingContext> {
    
    private static final Logger logger = LoggerFactory.getLogger(TracingVtxErrorHandler.class);
    
    private final List<RequestCtxDecorator> decorators = new ArrayList<>(1);
    
    public TracingVtxErrorHandler() {
        decorators.add(new MiddlewareSpanDecorator());
    }

    @Override
    public void handle(RoutingContext routingCtx) {
        if (logger.isTraceEnabled()) {
            logger.trace("Tracing Vtx Failure Handler invoked");
        }
        TracingHelper.windUp(routingCtx, decorators, true);
        
        // Call the next middleware/handler
        routingCtx.next();
    }
}
