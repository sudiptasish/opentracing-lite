/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.OTLAsyncScope;
import com.sc.hm.otl.core.RequestCtxDecorator;
import com.sc.hm.otl.util.OTLConstants;
import io.opentracing.Span;
import io.vertx.ext.web.RoutingContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
public final class TracingHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(TracingHelper.class);
    
    /**
     * 
     * @param routingCtx
     * @param decorators 
     */
    public static void windUp(RoutingContext routingCtx, List<RequestCtxDecorator> decorators) {
        windUp(routingCtx, decorators, false);
    }

    /**
     * 
     * @param routingCtx
     * @param decorators 
     * @param errorOccurred 
     */
    public static void windUp(RoutingContext routingCtx
        , List<RequestCtxDecorator> decorators
        , boolean errorOccurred) {
        
        OTLAsyncScope scope = (OTLAsyncScope)TracingVtxHandler.scope(routingCtx);
        
        if (scope != null) {
            int count = scope.spanCount();
            Span span = null;

            if (count > 1) {
                logger.warn("{} un-finished spans found in the scope of routing context: {}"
                    + ". Scope and span details: {}"
                    , count
                    , routingCtx
                    , scope);
            }
            for (int i = 0; i < count - 1; i ++) {
                span = scope.removeCurrent();
                span.setTag(OTLConstants.INCOMPLETE_TAG, Boolean.TRUE);

                span.finish();
            }
            // Pop the last element from the scope, which is the very first span,
            // and finish it gracefully.
            span = scope.removeCurrent();
            if (logger.isTraceEnabled()) {
                logger.trace("Closing the current span: {}", span.context());
            }
            for (RequestCtxDecorator decorator : decorators) {
                if (errorOccurred) {
                    decorator.onError(routingCtx.request()
                        , routingCtx.response()
                        , routingCtx.failure()
                        , span);
                }
                else {
                    decorator.onResponse(routingCtx.request()
                        , routingCtx.response()
                        , span);
                }
            }
            span.finish();
            routingCtx.remove(OTLConstants.VERTX_SCOPE);
        }
    }
}
