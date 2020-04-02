/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.filter;

import io.opentracing.Span;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Span decorator is used to decorate a span.
 * 
 * Decorating a span implies adding additional info about the span. Eventually 
 * they get stored as a tag.
 * Note that it is not mandatory to provide a span decorator. The {@link OTLFilter}
 * by default includes the {@link StandardFilterSpanDecorator}, which takes most
 * of the responsibility of decorating the newly created span. The user, however,
 * can provide their own decorator by specifying them in the web.xml file.
 *
 * @author Sudiptasish Chanda
 */
public interface FilterSpanDecorator {
    
    /**
     * This method will be invoked when the {@link OTLFilter} has just created the
     * span. The span is usually created immediately after intercepting the request.
     * 
     * @param request
     * @param span 
     */
    void onRequest(HttpServletRequest request, Span span);
    
    /**
     * This method will be invoked, when the {@link OTLFilter} is about to send
     * the final response to the client.
     * 
     * @param request
     * @param response
     * @param span 
     */
    void onResponse(HttpServletRequest request
        , HttpServletResponse response
        , Span span);
    
    /**
     * This method will be invoked, when the {@link OTLFilter} runs into some issue
     * while processing a client request.
     * 
     * @param request
     * @param response
     * @param e
     * @param span 
     */
    void onError(HttpServletRequest request
        , HttpServletResponse response
        , Throwable e
        , Span span);
}
