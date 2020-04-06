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
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

/**
 *
 * @author Sudiptasish Chanda
 */
public class WebClientSpanDecorator implements RequestCtxDecorator<HttpClientRequest, HttpClientResponse> {
    
    public static final String VERTX_CLIENT = "vertx.client";

    @Override
    public void onRequest(HttpClientRequest request, Span span) {
        Tags.COMPONENT.set(span, VERTX_CLIENT);
        Tags.HTTP_METHOD.set(span, request.method().name());
        Tags.HTTP_URL.set(span, request.path());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
    }

    @Override
    public void onResponse(HttpClientRequest request, HttpClientResponse response, Span span) {
        Tags.HTTP_STATUS.set(span, response.statusCode());
    }

    @Override
    public void onError(HttpClientRequest request
        , HttpClientResponse response
        , Throwable e
        , Span span) {
        
        Tags.ERROR.set(span, Boolean.TRUE);
        Tags.HTTP_STATUS.set(span, response.statusCode());
    }
    
}
