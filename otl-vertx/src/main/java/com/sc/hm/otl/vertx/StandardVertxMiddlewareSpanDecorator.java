/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * The default span decorator that comes along with the {@link OTLVertxHandler}.
 * 
 * @author Sudiptasish Chanda
 */
public class StandardVertxMiddlewareSpanDecorator implements VertxMiddlewareSpanDecorator {
    
    public static final String COMPONENT_VERTX_WEB = "vertx-web";

    @Override
    public void onRequest(HttpServerRequest request, Span span) {
        Tags.COMPONENT.set(span, COMPONENT_VERTX_WEB);
        Tags.HTTP_METHOD.set(span, request.rawMethod());
        Tags.HTTP_URL.set(span, request.path());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_SERVER);
    }

    @Override
    public void onResponse(HttpServerRequest request, HttpServerResponse response, Span span) {
        Tags.HTTP_STATUS.set(span, response.getStatusCode());
    }

    @Override
    public void onError(HttpServerRequest request
        , HttpServerResponse response
        , Throwable e
        , Span span) {
        
        Tags.HTTP_STATUS.set(span, response.getStatusCode());
    }
    
}
