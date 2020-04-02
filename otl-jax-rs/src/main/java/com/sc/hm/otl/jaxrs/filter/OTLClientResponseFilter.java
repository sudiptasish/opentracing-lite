/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import com.sc.hm.otl.core.OTLSyncScopeManager;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jax-rs client response filter.
 * 
 * A response filter is invoked when the downstream system has responded back.
 * Remember, that the span was originally set by the {@link OTLClientRequestFilter},
 * therefore, it's the responsibility of the response filter to finish the span.
 * 
 * Like the request filter, this filter, too, has to be explicitly registered with
 * the {@lick Client} first.
 * 
 * Example:
 * 
 * <pre>
 * {@code
 *     .....
 *     public Client restClient() {
 *         Client client = ClientBuilder
 *             .newBuilder()
 *             .register(new OTLClientRequestFilter())
 *             .register(new OTLClientResponseFilter())
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
 *         config.register(OTLClientResponseFilter.class);
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
public class OTLClientResponseFilter implements ClientResponseFilter {
    
    private final Logger logger = LoggerFactory.getLogger(OTLClientResponseFilter.class);
    
    private final Tracer tracer = GlobalTracer.get();  
    
    private final List<JaxRsClientFilterSpanDecorator> decorators = new ArrayList<>();
    
    public OTLClientResponseFilter() {
        decorators.add(new StandardJaxRsClientFilterSpanDecorator());
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
                    for (JaxRsClientFilterSpanDecorator decorator : decorators) {
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
