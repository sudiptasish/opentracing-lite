/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.filter;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The default span decorator that comes along with the {@link OTLFilter}.
 * 
 * @author Sudiptasish Chanda
 */
public class StandardFilterSpanDecorator implements FilterSpanDecorator {
    
    public static final String COMPONENT_SERVLET = "web-servlet";

    @Override
    public void onRequest(HttpServletRequest request, Span span) {
        Tags.COMPONENT.set(span, COMPONENT_SERVLET);
        Tags.HTTP_METHOD.set(span, request.getMethod());
        Tags.HTTP_URL.set(span, request.getRequestURL().toString());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_SERVER);
    }

    @Override
    public void onResponse(HttpServletRequest request, HttpServletResponse response, Span span) {
        Tags.HTTP_STATUS.set(span, response.getStatus());
    }

    @Override
    public void onError(HttpServletRequest request
        , HttpServletResponse response
        , Throwable e
        , Span span) {
        
        Tags.HTTP_STATUS.set(span, 500);
        Tags.ERROR.set(span, Boolean.TRUE);
    }
    
}
