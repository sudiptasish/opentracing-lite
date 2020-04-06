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
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.RequestCtxDecorator;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * Span decorator is used to decorate a span.
 * 
 * Decorating a span implies adding additional info about the span. Eventually 
 * they get stored as a tag.
 * Note that it is not mandatory to provide a span decorator. The {@link OTLVertxHandler}
 * by default includes the {@link StandardVertxMiddlewareSpanDecorator}, which takes most
 * of the responsibility of decorating the newly created span.
 *
 * @author Sudiptasish Chanda
 */
public class MiddlewareSpanDecorator implements RequestCtxDecorator<HttpServerRequest, HttpServerResponse> {
    
    public static final String VERTX_WEB = "vertx.web";

    @Override
    public void onRequest(HttpServerRequest request, Span span) {
        Tags.COMPONENT.set(span, VERTX_WEB);
        Tags.HTTP_METHOD.set(span, request.rawMethod());
        Tags.HTTP_URL.set(span, request.path());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_SERVER);
    }

    @Override
    public void onResponse(HttpServerRequest request, HttpServerResponse response, Span span) {
        Tags.HTTP_STATUS.set(span, response.getStatusCode());
    }

    @Override
    public void onError(HttpServerRequest request
        , HttpServerResponse response
        , Throwable e
        , Span span) {
        
        Tags.HTTP_STATUS.set(span, response.getStatusCode());
        Tags.ERROR.set(span, Boolean.TRUE);
    }
}
