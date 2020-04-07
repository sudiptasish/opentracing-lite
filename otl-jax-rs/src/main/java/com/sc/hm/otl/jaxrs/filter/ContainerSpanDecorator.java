/*
 *     Copyright 2020 Opentracing-LiTE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * If user decides not to use the otl TracingWebFilter, then another option to trap the
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
