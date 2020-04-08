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

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Spring react web client interceptor.
 * 
 * <p>
 * This interceptor must be used to intercept client-side HTTP requests. Implementations
 * of this interface can be registered with the {@link WebClient}, so as to 
 * modify the outgoing {@link HttpRequest} in order to inject the tracing headers.
 * 
 * <p>
 * As of today, the following headers will be injected:
 * 
 * <ul>
 * <li>X-B3-TraceId</li>
 * <li>X-B3-SpanId</li>
 * <li>X-B3-Sampled</li>
 * <li>X-B3-Baggage-{key}</li>
 * </ul>
 * 
 * <p>
 * As of Spring Framework 5, alongside the WebFlux stack, Spring introduced a new
 * HTTP client called WebClient. The WebClient is a modern, alternative HTTP client
 * to {@link RestTemplate}. Not only does it provide a traditional synchronous API,
 * but it also supports an efficient non-blocking and asynchronous approach.
 * 
 * WebClient supports the provision of adding a filter, that can intercept, examine,
 * and modify a client request (or response). Filters are very suitable for adding
 * functionality to every single request since the logic stays in one place. 
 * 
 * Note that, unlike {@link RestClientInterceptor}, it does not have the provision
 * of creating a span. It's only job is to inject the tracing headers into the 
 * outboud client request.
 * 
 * The filter can be added as below:
 * <pre>
 * {@code 
 *     WebClient webClient = WebClient.builder()
 *         .filter(new WebClientInterceptor())
 *         .build();
 * }
 * </pre>
 * 
 * If a Webclient is initialized just by <code>WebClient webClient = WebClient.create()</code>
 * then it does not give us the provision to add the new interceptor. Therefore
 * replace all such calls with Webclient builder.
 * 
 * You can, however, define the WebClient like below:
 * <pre>
 * {@code 
 *     
 *     public WebClient defaultWebClient() {
 *         return WebClient.builder()
 *             .baseUrl(BASE_URL)
 *             ....
 *             ....
 *             ....
 *             .build(); 
 *     }
 * }
 * </pre>
 *
 * This will trigger the custom bean post processor, which will ensure to inject the
 * the appropriate filter/interceptor into the {@link WebClient}.
 * 
 * @author Sudiptasish Chanda
 */
public class WebClientInterceptor implements ExchangeFilterFunction {
    
    private final Logger logger = LoggerFactory.getLogger(RestClientInterceptor.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction ef) {
        // Default behaavior.
        // Span creation isdisabled. Therefore just extract the span context
        // from the currently active span and propagate.
        // If no active span, then nothing will be propagated.
        Span span = tracer.activeSpan();
        if (span != null) {
            tracer.inject(span.context()
                , Format.Builtin.TEXT_MAP_INJECT
                , new HttpHeaderAdapter(request.headers()));
            
            if (logger.isTraceEnabled()) {
                logger.trace("Span creation is disabled in WebClient filter. Therefore propagating the"
                    + " current contextual info from the span: {}", span.context().toSpanId());
            }
        }
        else {
            if (logger.isTraceEnabled()) {
                logger.trace("No Active span exist. Context won't be propagated");
            }
        }
        return ef.exchange(request);
    }
}
