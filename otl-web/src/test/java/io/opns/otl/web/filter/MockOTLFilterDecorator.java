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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sudiptasish Chanda
 */
public class MockOTLFilterDecorator implements RequestCtxDecorator<HttpServletRequest, HttpServletResponse> {

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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
