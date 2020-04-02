/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;

/**
 * Pltaform provided jax-rs client side decorator.
 *
 * @author Sudiptasish Chanda
 */
public class StandardJaxRsClientFilterSpanDecorator implements JaxRsClientFilterSpanDecorator {
    
    public static final String COMPONENT_JAX_RS_CLIENT = "jax-rs-rest-client";

    @Override
    public void onRequest(ClientRequestContext requestContext, Span span) {
        Tags.COMPONENT.set(span, COMPONENT_JAX_RS_CLIENT);
        Tags.HTTP_METHOD.set(span, requestContext.getMethod());
        Tags.HTTP_URL.set(span, requestContext.getUri().getPath());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
    }

    @Override
    public void onResponse(ClientRequestContext requestContext
        , ClientResponseContext responseContext
        , Span span) {
        
        Tags.HTTP_STATUS.set(span, responseContext.getStatus());
    }
    
}
