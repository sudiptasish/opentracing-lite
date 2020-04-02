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
 *
 * @author Sudiptasish Chanda
 */
public class MockOTLFilterDecorator implements FilterSpanDecorator {

    @Override
    public void onRequest(HttpServletRequest request, Span span) {
        span.setTag("mock.request", "true");
    }

    @Override
    public void onResponse(HttpServletRequest request, HttpServletResponse response, Span span) {
        span.setTag("mock.response", "true");
    }

    @Override
    public void onError(HttpServletRequest request, HttpServletResponse response, Throwable e, Span span) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
