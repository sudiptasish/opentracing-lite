/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.spring.client;

import com.sc.hm.otl.core.RequestCtxDecorator;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Span decorator is used to decorate a span.
 * 
 * Decorating a span implies adding additional info about the span. Eventually 
 * they get stored as a tag.
 * Note that it is not mandatory to provide a span decorator. The {@link RestClientInterceptor}
 * by default includes the {@link TemplateSpanDecorator}, which takes most
 * of the responsibility of decorating the newly created span. The user, however,
 * can provide their own decorator.
 * 
 * Note that this is a client side decorator.
 *
 * @author Sudiptasish Chanda
 */
public class TemplateSpanDecorator implements RequestCtxDecorator<HttpRequest, ClientHttpResponse> {

    public static final String SPRING_CLIENT = "spring-client";

    @Override
    public void onRequest(HttpRequest request, Span span) {
        Tags.COMPONENT.set(span, SPRING_CLIENT);
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
