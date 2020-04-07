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
import io.opentracing.Span;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracing web client response handler.
 * 
 * <p>
 * This response handler will be invoked, when the {@link WebClient} has received
 * a response from the called service.
 * 
 *
 * @author Sudiptasish Chanda
 */
public class TracingResponseHandler implements Handler<AsyncResult<HttpResponse<Buffer>>> {
    
    private static final Logger logger = LoggerFactory.getLogger(TracingResponseHandler.class);
    
    private final List<RequestCtxDecorator> decorators = new ArrayList<>(1);
    
    protected final RoutingContext routingCtx;
    
    public TracingResponseHandler(RoutingContext routingCtx) {
        this.routingCtx = routingCtx;
        decorators.add(new WebClientSpanDecorator());
    }

    @Override
    public void handle(AsyncResult<HttpResponse<Buffer>> result) {
        Span span = TracingVtxHandler.removeActiveSpan(routingCtx);
        
        if (logger.isTraceEnabled()) {
            logger.trace("TracingResponseHandler is invoked. Current span: " + span);
        }
        if (span != null) {
            if (result.succeeded()) {
                for (RequestCtxDecorator decorator : decorators) {
                    decorator.onResponse(null
                        , result.result()
                        , span);
                }
            }
            else {
                for (RequestCtxDecorator decorator : decorators) {
                    decorator.onError(null
                        , result.result()
                        , result.cause()
                        , span);
                }
            }
            span.finish();
        }
        process(result);
    }
    
    /**
     * Finish the processing.
     * @param result    the final result object.
     */
    protected void process(AsyncResult<HttpResponse<Buffer>> result) {
        // Empty implementation
    }
}
