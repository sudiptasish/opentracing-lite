/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import com.sc.hm.otl.core.RequestCtxDecorator;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;

/**
 * Interface that represents a span decorator for jax rs.
 * If user decides not to use the {@OTLFilter}, then another option to trap the
 * incoming request is to use a {@link ContainerRequestFilter}. Once the request
 * is intercepted, then this decorator must be used to decorate the newly created
 * span.
 *
 * @author Sudiptasish Chanda
 */
public class ContainerSpanDecorator implements RequestCtxDecorator<ContainerRequestContext, ContainerResponseContext> {
    
    public static final String JAX_RS = "jaxrs.web";

    @Override
    public void onRequest(ContainerRequestContext requestContext, Span span) {
        Tags.COMPONENT.set(span, JAX_RS);
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

    @Override
    public void onError(ContainerRequestContext request
        , ContainerResponseContext response
        , Throwable e
        , Span span) {
        
        Tags.HTTP_STATUS.set(span, response.getStatus());
        Tags.ERROR.set(span, Boolean.TRUE);
    }
}
