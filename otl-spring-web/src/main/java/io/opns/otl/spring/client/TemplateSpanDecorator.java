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
package io.opns.otl.spring.client;

import io.opns.otl.core.RequestCtxDecorator;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Span decorator is used to decorate a span.
 * 
 * Decorating a span implies adding additional info about the span. Eventually 
 * they get stored as a tag.
 * Note that it is not mandatory to provide a span decorator. The {@link RestClientInterceptor}
 * by default includes the {@link TemplateSpanDecorator}, which takes most
 * of the responsibility of decorating the newly created span. The user, however,
 * can provide their own decorator.
 * 
 * Note that this is a client side decorator.
 *
 * @author Sudiptasish Chanda
 */
public class TemplateSpanDecorator implements RequestCtxDecorator<HttpRequest, ClientHttpResponse> {

    public static final String SPRING_CLIENT = "spring-client";

    @Override
    public void onRequest(HttpRequest request, Span span) {
        Tags.COMPONENT.set(span, SPRING_CLIENT);
        Tags.HTTP_METHOD.set(span, request.getMethod().toString());
        Tags.HTTP_URL.set(span, request.getURI().toString());
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
    }

    @Override
    public void onResponse(HttpRequest request
        , ClientHttpResponse response
        , Span span) {
        
        try {
            Tags.HTTP_STATUS.set(span, response.getRawStatusCode());
        }
        catch (IOException e) {
            // Do Nothing, maybe the status code is not yet generated.
        }
    }

    @Override
    public void onError(HttpRequest request
        , ClientHttpResponse response
        , Throwable error
        , Span span) {
        
        Tags.ERROR.set(span, Boolean.TRUE);
    }
}
