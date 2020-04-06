/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import com.sc.hm.otl.core.OTLSyncScopeManager;
import com.sc.hm.otl.core.RequestCtxDecorator;
import com.sc.hm.otl.util.OTLConstants;
import com.sc.hm.otl.util.ObjectCreator;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
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
import javax.ws.rs.ext.WriterInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * As per jax-rs specification, there are three ways to intercept/filter the incoming
 * request: one by using the standard servlet filter, {@link OTLFilter}, secondly,
 * by creating a {@link ContainerRequestFilter}, and finally, by creating a {@link WriterInterceptor}.
 * 
 * <p>
 * Interceptors share a common API for the server and the client side. Whereas filters
 * are primarily intended to manipulate request and response parameters like HTTP 
 * headers, URIs and/or HTTP methods, interceptors are intended to manipulate entities,
 * via manipulating entity input/output streams. In case of tracing, the state of
 * the request body is never changed. Therefore, having a request filter makes more
 * sense to intercept incoming request.
 * 
 * <p>
 * The request filter can either be added in the <code>web.xml</code> file, or via
 * {@link Provider} annotation. Here we are skipping the provider annotation, in
 * case user wants to use the {@link OTLFilter} by specifying the same in the <code>web.xml</code>.
 * 
 * Note that, Providers are a simply a way of extending and customizing the JAX-RS
 * runtime. You can think of them as plugins that (potentially) alter the behavior
 * of the runtime, in order to accomplish a set of (program defined) goals. If this
 * class is annotated with {@link Provider}, and the {@link OTLFilter} is also added
 * to the web.xml, then multiple span will be created for the same request. Hence, it
 * is recommended to add the {@link TracingContainerFilter} in the <code>web.xml</code>.
 * 
 * <p>
 * For example, in Jersey, you can specify the provider as below:
 * <pre>
 * {@code
 <web-app>
     .....
     <servlet>  
         <servlet-name>Jersey REST Service</servlet-name>  
         <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>  
         <init-param>
             <param-name>com.sun.jersey.spi.container.ContainerRequestFilters</param-name>
             <param-value>com.sc.hm.otl.jaxrs.filter.TracingContainerFilter</param-value>
         </init-param>
         <init-param>
             <param-name>com.sun.jersey.spi.container.ContainerRequestFilters</param-name>
             <param-value>com.sc.hm.otl.jaxrs.filter.TracingContainerFilter</param-value>
         </init-param>
         <init-param>  
             <param-name>jersey.config.server.provider.packages</param-name>  
             <param-value>com.sc.hm.otl.jaxrs.filter,package.name.for.other.rest.classes</param-value>  
         </init-param>  
         <init-param>  
             <param-name>com.sun.jersey.api.json.POJOMappingFeature</param-name>  
             <param-value>true</param-value>  
         </init-param>      
         <load-on-startup>1</load-on-startup>  
     </servlet> 
     .....
     .....
 </web-app>
 }
 * </pre>
 *
 * @author Sudiptasish Chanda
 */
@Provider
public class TracingContainerFilter implements ContainerRequestFilter, ContainerResponseFilter {
    
    private final Logger logger = LoggerFactory.getLogger(TracingContainerFilter.class);

    private final Tracer tracer = GlobalTracer.get();
    
    private final List<RequestCtxDecorator> decorators = new ArrayList<>();
    private final List<Pattern> skipPatterns = new ArrayList<>();
    
    @Resource
    private ServletContext ctx;
    
    public TracingContainerFilter() {
        decorators.add(new ContainerSpanDecorator());
        
        if (ctx != null) {
            // Check if a custom decorator is provided.
            String decoratorClass = ctx.getInitParameter(OTLConstants.DECORATOR);
            if (decoratorClass != null && (decoratorClass = decoratorClass.trim()).length() > 0) {
                // Initialize and add the decorator.
                decorators.add(initDecorator(decoratorClass));
            }

            // Check the presence of a skip pattern.
            String skipPattern = ctx.getInitParameter(OTLConstants.SKIP_PATTERN);
            if (skipPattern != null && (skipPattern = skipPattern.trim()).length() > 0) {
                String[] patterns = skipPattern.split(",");
                for (String pattern : patterns) {
                    skipPatterns.add(Pattern.compile(pattern.trim()));
                }
            }
        }
    }

    /**
     * Create and initialize the custom span decorator. This span decorator will
     * be added after {@code StandardFilterSpanDecorator} and
     * platform provided decorator {@code FilterSpanDecorator}.
     *
     * @param decoratorClass Custom decorator class.
     * @return ContainerSpanDecorator
     */
    private ContainerSpanDecorator initDecorator(String decoratorClass) {
        return ObjectCreator.create(decoratorClass);
    }

    @Override
    public void filter(ContainerRequestContext requestCtx) throws IOException {
        if (!isTraceable(requestCtx)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Request with uri [{}] is not traceable"
                    , requestCtx.getUriInfo().getPath());
            }
            return;
        }
        // Now start tracing the request.
        // Create a new span after extracting the span context from the request.
        SpanContext context = tracer.extract(Format.Builtin.TEXT_MAP_EXTRACT
            , new RequestHeaderCarrier(requestCtx));
        
        // Create the span.
        // Creation of span does not necessarily create the scope. Scope indicates
        // the work being done by the current thread at any given point of time.
        // And if that work is to represented by a span, then the span should be
        // explicitly set in the current scope before executing the task.
        Span span = tracer.buildSpan(requestCtx.getMethod())
            .asChildOf(context)
            .start();
        
        if (logger.isTraceEnabled()) {
            logger.trace("JaxRs Filter created new Span: {}", span.context());
        }
        
        for (RequestCtxDecorator decorator : decorators) {
            decorator.onRequest(requestCtx, span);
        }
        
        // Jax Rs container request filter is different than {@link Filter}.
        // Unlike conventiona web filter, where the same filter handles the response,
        // Jax Rs delegates that responsibility to {@link ContainerResponseFilter}.
        // So the span that is created here must be closed in the response filter.
        // Here, we just need to activate the span, nothing more, nothing less.
        
        // Note: it is assumed that every call in jax rs container is synchronous.
        tracer.activateSpan(span);
    }

    @Override
    public void filter(ContainerRequestContext requestCtx
        , ContainerResponseContext responseCtx) throws IOException {
        
        // The span is already created and activated by the {@link OTLRequestFilter}.
        // Here we will retrieve the span from the current thread context and
        // finish it.
        Span span = null;
        
        try (Scope scope = ((OTLSyncScopeManager)tracer.scopeManager()).active()) {
            span = tracer.activeSpan();
            if (span != null) {
                for (RequestCtxDecorator decorator : decorators) {
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

    /**
     * Check if the current URL path needs to be traced.
     * 
     * @param request
     * @param response
     * @return boolean
     */
    private boolean isTraceable(ContainerRequestContext requestCtx) {
        if (!skipPatterns.isEmpty()) {
            boolean skipped = false;
            
            String uri = requestCtx.getUriInfo().getPath();
            for (Pattern skipPattern : skipPatterns) {
                skipped = skipPattern.matcher(uri).matches();
                if (skipped) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }
    
}
