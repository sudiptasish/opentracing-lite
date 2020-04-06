/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.filter;

import com.sc.hm.otl.core.RequestCtxDecorator;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Span decorator is used to decorate a span.
 * 
 * Decorating a span implies adding additional info about the span. Eventually 
 * they get stored as a tag.
 * Note that it is not mandatory to provide a span decorator. The {@link OTLFilter}
 * by default includes the {@link FilterSpanDecorator}, which takes most
 * of the responsibility of decorating the newly created span. The user, however,
 * can provide their own decorator by specifying them in the web.xml file.
 *
 * @author Sudiptasish Chanda
 */
public class FilterSpanDecorator implements RequestCtxDecorator<HttpServletRequest, HttpServletResponse> {
    
    public static final String WEB_SERVLET = "j2ee.web";

    @Override
    public void onRequest(HttpServletRequest request, Span span) {
        Tags.COMPONENT.set(span, WEB_SERVLET);
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
