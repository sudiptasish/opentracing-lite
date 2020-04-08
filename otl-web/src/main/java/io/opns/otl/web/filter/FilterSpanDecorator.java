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
package io.opns.otl.web.filter;

import io.opns.otl.core.RequestCtxDecorator;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Span decorator is used to decorate a span.
 * 
 * Decorating a span implies adding additional info about the span. Eventually 
 * they get stored as a tag.
 * Note that it is not mandatory to provide a span decorator. The {@link TracingWebFilter}
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
