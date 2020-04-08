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
package io.opns.otl.vertx;

import io.opns.otl.core.OTLAsyncScope;
import io.opns.otl.core.RequestCtxDecorator;
import io.opns.otl.util.OTLConstants;
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
     * Finish the span that is currently present in the routing context.
     * 
     * @param routingCtx    Vertx routing context object.
     * @param decorators    Vertx span decorators.
     */
    public static void windUp(RoutingContext routingCtx, List<RequestCtxDecorator> decorators) {
        windUp(routingCtx, decorators, false);
    }

    /**
     * Finish the span(s).
     * 
     * @param routingCtx    Vertx routing context object.
     * @param decorators    Vertx span decorators.
     * @param errorOccurred Indicate whether the current call has run into some error.
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
