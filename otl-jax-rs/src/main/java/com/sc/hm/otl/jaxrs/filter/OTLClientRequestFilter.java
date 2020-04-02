/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jax-rs Client filters are similar to container request filters.
 * 
 * <p>
 * for any client request invoked from the client API the client request filters
 * {@link ClientRequestFilter} are executed that could manipulate the request. 
 * If not aborted, the outcoming request is then physically sent over to the server
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
 * the {@link OTLClientResponseFilter} once it receives the response from the 
 * remote service. User, however, can override the default
 * behavior by specifying the system property <code>-Djax.rs.span</code> 
 * as false. In which case, no new span will be created, but only the contextual
 * data will be passed.
 * 
 * The filter has to be explicitly registered with the {@lick Client} first.
 * Example:
 * 
 * <pre>
 * {@code
 *     .....
 *     public Client restClient() {
 *         Client client = ClientBuilder
 *             .newBuilder()
 *             .register(new OTLClientRequestFilter())
 *             .build();
 *     }
 * 
 *     ....
 *     ....
 * 
 *     public void getName() {
 *         Client client = restClient();
 *         client.target("url")
 *             .request()
 *             .get(String.class)
 *     }
 * }
 * </pre>
 * 
 * You can also register the request filter via {@link ClientConfig} as a provider
 * and use it like below:
 * 
 * <pre>
 * {@code
 *     .....
 *     public Client restClient() {
 *         ClientConfig config = new ClientConfig();
 *         config.register(OTLClientRequestFilter.class);
 *         return ClientBuilder.newClient(config);
 *     }
 * 
 *     ....
 *     ....
 * 
 *     public void getName() {
 *         Client client = restClient();
 *         client.target("url")
 *             .request()
 *             .get(String.class)
 *     }
 * }
 * </pre>
 *
 * @author Sudiptasish Chanda
 */
@Provider
public class OTLClientRequestFilter implements ClientRequestFilter {
    
    private final Logger logger = LoggerFactory.getLogger(OTLClientRequestFilter.class);
    
    private final Tracer tracer = GlobalTracer.get();
    
    private final List<JaxRsClientFilterSpanDecorator> decorators = new ArrayList<>();
    
    public OTLClientRequestFilter() {
        decorators.add(new StandardJaxRsClientFilterSpanDecorator());
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
                , new ClientRequestAdapter(requestCtx));

            for (JaxRsClientFilterSpanDecorator decorator : decorators) {
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
                    , new ClientRequestAdapter(requestCtx));
                
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
}
