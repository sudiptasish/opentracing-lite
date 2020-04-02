/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

/**
 * Pltaform provide decorator for jax rs filter.
 *
 * @author Sudiptasish Chanda
 */
public class StandardJaxRsFilterSpanDecorator implements JaxRsFilterSpanDecorator {
    
    public static final String COMPONENT_JAX_RS = "jaxrs-server";

    @Override
    public void onRequest(ContainerRequestContext requestContext, Span span) {
        Tags.COMPONENT.set(span, COMPONENT_JAX_RS);
        Tags.HTTP_METHOD.set(span, requestContext.getMethod());
        Tags.HTTP_URL.set(span, requestContext.getUriInfo().getPath());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_SERVER);
    }

    @Override
    public void onResponse(ContainerRequestContext requestContext
        , ContainerResponseContext responseContext
        , Span span) {
        
        Tags.HTTP_STATUS.set(span, responseContext.getStatus());
    }
    
}
