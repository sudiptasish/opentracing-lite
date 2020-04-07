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
package com.sc.hm.otl.spring.client;

import io.opentracing.Span;
import org.springframework.web.reactive.function.client.ClientRequest;

/**
 * Decorator responsible for decorating a span while making a client call using
 * spring react web client.
 * 
 * Currently {@link WebClientInterceptor} does not support creating a new span at
 * the time of making outbound call. It's job is to propagate the contextual info
 * to the next service. Therefore the decorator is InActive today.
 *
 * @author Sudiptasish Chanda
 */
public interface WebClientSpanDecorator {
    
    /**
     * This API will be invoked just before sending the response to next service.
     * 
     * @param request   The request object
     * @param span      The span created by the interceptor component.
     */
    void onRequest(ClientRequest request, Span span);
}
