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
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * As per jax-rs specification, one needs a {@link ContainerRequestFilter} and a
 * {@link ContainerResponseFilter} to manipulate the request/response.
 * 
 * <p>
 * Whatever span created and set by the {@link ContainerRequestFilter}, will be 
 * closed by this filter. Jax Rs will ensure to invoke the response filter to
 * mark the end of a request.
 * 
 * <p>
 * For example, in Jersey, you can specify the provider as below:
 * <pre>
 * {@code
 * <web-app>
 *     .....
 *     <servlet>  
 *         <servlet-name>Jersey REST Service</servlet-name>  
 *         <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>  
 *         <init-param>
 *             <param-name>com.sun.jersey.spi.container.ContainerRequestFilters</param-name>
 *             <param-value>com.sc.hm.otl.jaxrs.filter.OTLRequestFilter</param-value>
 *         </init-param>
 *         <init-param>
 *             <param-name>com.sun.jersey.spi.container.ContainerRequestFilters</param-name>
 *             <param-value>com.sc.hm.otl.jaxrs.filter.OTLResponseFilter</param-value>
 *         </init-param>
 *         <init-param>  
 *             <param-name>jersey.config.server.provider.packages</param-name>  
 *             <param-value>com.sc.hm.otl.jaxrs.filter,package.name.for.other.rest.classes</param-value>  
 *         </init-param>  
 *         <init-param>  
 *             <param-name>com.sun.jersey.api.json.POJOMappingFeature</param-name>  
 *             <param-value>true</param-value>  
 *         </init-param>      
 *         <load-on-startup>1</load-on-startup>  
 *     </servlet> 
 *     .....
 *     .....
 * </web-app>
 * }
 * </pre>
 *
 * @author Sudiptasish Chanda
 */
@Provider
public class OTLResponseFilter implements ContainerResponseFilter {
    
    private final Logger logger = LoggerFactory.getLogger(OTLResponseFilter.class);

    private final Tracer tracer = GlobalTracer.get();
    
    private final List<JaxRsFilterSpanDecorator> decorators = new ArrayList<>();
    private final List<Pattern> skipPatterns = new ArrayList<>();
    
    @Resource
    private ServletContext ctx;
    
    public OTLResponseFilter() {
        decorators.add(new StandardJaxRsFilterSpanDecorator());
    }

    @Override
    public void filter(ContainerRequestContext requestCtx, ContainerResponseContext responseCtx) throws IOException {
        // The span is already created and activated by the {@link OTLRequestFilter}.
        // Here we will retrieve the span from the current thread context and
        // finish it.
        Span span = null;
        
        try (Scope scope = ((OTLSyncScopeManager)tracer.scopeManager()).active()) {
            span = tracer.activeSpan();
            if (span != null) {
                for (JaxRsFilterSpanDecorator decorator : decorators) {
                    decorator.onResponse(requestCtx, responseCtx, span);
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("JaxRs Filter will finish the Span: {}", span.context());
                }
            }
            else {
                if (logger.isTraceEnabled()) {
                    logger.trace("JaxRs Filter did not find any active span");
                }
            }
        }
        finally {
            if (span != null) {
                span.finish();
            }
        }
    }
    
}
