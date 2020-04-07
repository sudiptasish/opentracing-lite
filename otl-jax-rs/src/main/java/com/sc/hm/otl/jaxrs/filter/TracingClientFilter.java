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

import com.sc.hm.otl.core.OTLSyncScopeManager;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jax-rs Client filters are similar to container request filters.
 * 
 * <p>
 * for any client request invoked from the client API the client request filters
 * {@link ClientRequestFilter} are executed that could manipulate the request. 
 * If not aborted, the outgoing request is then physically sent over to the server
 * side and once a response is received back from the server the client response
 * filters (ClientResponseFilter) are executed that might again manipulate the
 * returned response. Finally the response is passed back to the code that invoked
 * the request. If the request was aborted in any client request filter then the
 * client/server communication is skipped and the aborted response is used in the
 * response filters.
 * 
 * <p>
 * Note that there is also a concept of Interceptors in jax-rs specification. But
 * they are more connected with the marshalling and unmarshalling of the HTTP message
 * bodies that are contained in the requests and the responses. Note that they are 
 * executed after the filters and only if a message body is present. Hence it is
 * decided to use client filter as they are more suitable for this purpose.
 * 
 * <p>
 * The client request filter will always create a new span to pass the newly
 * created span context (namely the traceId, spanId and the baggage items, if any)
 * to the next service on the chain. The same span would then be closed later by
 * the {@link TracingClientFilter} once it receives the response from the 
 * remote service. User, however, can override the default
 * behavior by specifying the system property <code>-Djax.rs.span</code> 
 * as false. In which case, no new span will be created, but only the contextual
 * data will be passed.
 * 
 * The filter has to be explicitly registered with the {@link Client} first.
 * Example:
 * 
 * <pre>
 * {@code
     .....
     public Client restClient() {
         Client client = ClientBuilder
             .newBuilder()
             .register(new TracingClientFilter())
             .build();
     }
 
     ....
     ....
 
     public void getName() {
         Client client = restClient();
         client.target("url")
             .request()
             .get(String.class)
     }
 }
 * </pre>
 * 
 * You can also register the request filter via ClientConfig as a provider
 * and use it like below:
 * 
 * <pre>
 * {@code
     .....
     public Client restClient() {
         ClientConfig config = new ClientConfig();
         config.register(TracingClientFilter.class);
         return ClientBuilder.newClient(config);
     }
 
     ....
     ....
 
     public void getName() {
         Client client = restClient();
         client.target("url")
             .request()
             .get(String.class)
     }
 }
 * </pre>
 *
 * @author Sudiptasish Chanda
 */
@Provider
public class TracingClientFilter implements ClientRequestFilter, ClientResponseFilter {
    
    private final Logger logger = LoggerFactory.getLogger(ClientRequestFilter.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    private final List<ClientSpanDecorator> decorators = new ArrayList<>();
    
    public TracingClientFilter() {
        decorators.add(new ClientSpanDecorator());
    }

    @Override
    public void filter(ClientRequestContext requestCtx) throws IOException {
        Boolean enableSpan = Boolean.parseBoolean(System.getProperty("jax.rs.span", "true"));
        
        if (logger.isTraceEnabled()) {
            logger.trace("Value of jax.rs.span is: " + enableSpan);
        }
        if (enableSpan) {
            // The currently active span would become the parent span of this
            // newly created span.
            Span span = tracer.buildSpan(requestCtx.getMethod()).start();
            
            if (logger.isTraceEnabled()) {
                logger.trace("Created new Jax-rs client span: {}", span.context());
            }
            tracer.inject(span.context()
                , Format.Builtin.TEXT_MAP_INJECT
                , new RequestHeaderAdapter(requestCtx));

            for (ClientSpanDecorator decorator : decorators) {
                decorator.onRequest(requestCtx, span);
            }
            // Jax Rs client request filter is different than any other client side interceptor.
            // Unlike conventiona interceptor, where the same interceptor handles the response,
            // Jax Rs delegates that responsibility to {@link ClientResponseFilter}.
            // So the span that is created here must be closed in the client response filter.
            // Here, we just need to activate the span, nothing more, nothing less.

            // Note: it is assumed that every call in jax rs container is synchronous.
            tracer.activateSpan(span);
        }
        else {
            // Default behavior.
            // Span creation isdisabled. Therefore just extract the span context
            // from the currently active span and propagate.
            // If no active span, then nothing will be propagated.
            Span span = tracer.activeSpan();
            
            if (span != null) {
                tracer.inject(span.context()
                    , Format.Builtin.TEXT_MAP_INJECT
                    , new RequestHeaderAdapter(requestCtx));
                
                if (logger.isTraceEnabled()) {
                    logger.trace("Span creation is disabled in Jax-rs client. Therefore propagating the"
                        + " current contextual info from the span: {}", span.context().toSpanId());
                }
            }
            else {
                if (logger.isTraceEnabled()) {
                    logger.trace("No Active span exist. Context won't be propagated");
                }
            }
        }
    }

    @Override
    public void filter(ClientRequestContext requestCtx, ClientResponseContext responseCtx) throws IOException {
        Boolean enableSpan = Boolean.parseBoolean(System.getProperty("jax.rs.span", "true"));
        
        if (enableSpan) {
            // The span is already created and activated by the {@link OTLRequestFilter}.
            // Here we will retrieve the span from the current thread context and
            // finish it.
            Span span = null;
            
            try (Scope scope = ((OTLSyncScopeManager)tracer.scopeManager()).active()) {
                span = tracer.activeSpan();
                if (span != null) {
                    for (ClientSpanDecorator decorator : decorators) {
                        decorator.onResponse(requestCtx, responseCtx, span);
                    }
                    if (logger.isTraceEnabled()) {
                        logger.trace("JaxRs Client Filter will finish the Span: {}", span.context());
                    }
                }
                else {
                    if (logger.isTraceEnabled()) {
                        logger.trace("JaxRs Client Filter did not find any active span");
                    }
                }
            }
            finally {
                if (span != null) {
                    span.finish();
                }
            }
        }
        else {
            if (logger.isTraceEnabled()) {
                logger.trace("Jax-rs Client side span creation is disabled.");
            }
        }
    }
}
