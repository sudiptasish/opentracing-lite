/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.RequestCtxDecorator;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

/**
 *
 * @author Sudiptasish Chanda
 */
public class WebClientSpanDecorator implements RequestCtxDecorator<HttpClientRequest, HttpClientResponse> {
    
    public static final String VERTX_CLIENT = "vertx.client";

    @Override
    public void onRequest(HttpClientRequest request, Span span) {
        Tags.COMPONENT.set(span, VERTX_CLIENT);
        Tags.HTTP_METHOD.set(span, request.method().name());
        Tags.HTTP_URL.set(span, request.path());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
    }

    @Override
    public void onResponse(HttpClientRequest request, HttpClientResponse response, Span span) {
        Tags.HTTP_STATUS.set(span, response.statusCode());
    }

    @Override
    public void onError(HttpClientRequest request
        , HttpClientResponse response
        , Throwable e
        , Span span) {
        
        Tags.ERROR.set(span, Boolean.TRUE);
        Tags.HTTP_STATUS.set(span, response.statusCode());
    }
    
}
