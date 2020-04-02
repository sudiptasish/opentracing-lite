/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.spring.client;

import io.opentracing.Span;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Span decorator is used to decorate a span.
 * 
 * Decorating a span implies adding additional info about the span. Eventually 
 * they get stored as a tag.
 * Note that it is not mandatory to provide a span decorator. The {@link OTLRestTemplateInterceptor}
 * by default includes the {@link StandardRestTemplateSpanDecorator}, which takes most
 * of the responsibility of decorating the newly created span. The user, however,
 * can provide their own decorator.
 * 
 * Note that this is a client side decorator.
 *
 * @author Sudiptasish Chanda
 */
public interface RestTemplateSpanDecorator {
    
    /**
     * This method will be invoked just before sending the outbound request and
     * post creating the span. This will ensure to setup any additional tag before
     * propagating the contextual info.
     * 
     * @param request
     * @param span 
     */
    void onRequest(HttpRequest request, Span span);
    
    /**
     * This method will be called once the remote service has sent back the response.
     * One can further decorate the span by associating appropriate response data.
     * 
     * @param request
     * @param response
     * @param span 
     */
    void onResponse(HttpRequest request, ClientHttpResponse response, Span span);
    
    /**
     * In case the remote call fails, this API will be called with the error object.
     * 
     * @param request
     * @param response
     * @param error
     * @param span 
     */
    void onError(HttpRequest request, ClientHttpResponse response, Throwable error, Span span);
}
