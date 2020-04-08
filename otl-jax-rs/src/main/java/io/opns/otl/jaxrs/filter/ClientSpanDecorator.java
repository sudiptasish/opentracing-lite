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
package io.opns.otl.jaxrs.filter;

import io.opns.otl.core.RequestCtxDecorator;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;

/**
 * Interface that represents a span decorator for jax rs client.
 * 
 * This decorator will be called while propagating the span context to the next
 * service in the chain.
 *
 * @author Sudiptasish Chanda
 */
public class ClientSpanDecorator implements RequestCtxDecorator<ClientRequestContext, ClientResponseContext> {
    
    public static final String JAX_RS_CLIENT = "jaxrs.client";

    @Override
    public void onRequest(ClientRequestContext requestContext, Span span) {
        Tags.COMPONENT.set(span, JAX_RS_CLIENT);
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

    @Override
    public void onError(ClientRequestContext request
        , ClientResponseContext response
        , Throwable e
        , Span span) {
        
        Tags.HTTP_STATUS.set(span, response.getStatus());
        Tags.ERROR.set(span, Boolean.TRUE);
    }
}
