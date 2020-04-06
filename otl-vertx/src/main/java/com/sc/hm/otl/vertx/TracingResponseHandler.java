/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
     * @param result 
     */
    protected void process(AsyncResult<HttpResponse<Buffer>> result) {
        // Empty implementation
    }
}
