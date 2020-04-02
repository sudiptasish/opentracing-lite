/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.spring.client;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Platfor provided span decoratoro for spring rest template.
 *
 * @author Sudiptasish Chanda
 */
public class StandardRestTemplateSpanDecorator implements RestTemplateSpanDecorator {

    public static final String COMPONENT_REST_TEPLATE = "spring-rest-client";

    @Override
    public void onRequest(HttpRequest request, Span span) {
        Tags.COMPONENT.set(span, COMPONENT_REST_TEPLATE);
        Tags.HTTP_METHOD.set(span, request.getMethod().toString());
        Tags.HTTP_URL.set(span, request.getURI().toString());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
    }

    @Override
    public void onResponse(HttpRequest request
        , ClientHttpResponse response
        , Span span) {
        
        try {
            Tags.HTTP_STATUS.set(span, response.getRawStatusCode());
        }
        catch (IOException e) {
            // Do Nothing, maybe the status code is not yet generated.
        }
    }

    @Override
    public void onError(HttpRequest request
        , ClientHttpResponse response
        , Throwable error
        , Span span) {
        
        Tags.ERROR.set(span, Boolean.TRUE);
    }
    
}
