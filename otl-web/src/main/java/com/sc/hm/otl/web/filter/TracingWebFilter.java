/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.filter;

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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Filter that must be deployed in the container by the app that intents to use
 * the opentracing lite implementation.
 * 
 * <br/>
 * The <code>TracingWebFilter</code> will intercept any incoming http request and  set
 * the appropriate parameters in the tracing context. These parameters and their
 * values will be used to trace a request flow. Once the filter returns the response
 * to the client it will dump this information as a span.log.
 * <br/>
 * The Filter provides a default constructor. In case the filter is initialized
 * via the lifecycle APIs of the container, the {@code javax.servlet.Filter#init(javax.servlet.FilterConfig)}
 * method will take care of setting the necessary decorator(s) and skip pattern
 * in the context, so that these can be used by this filter at the time of tracing a request.
 * <br/>
 * </p>
 *
 * <br/>
 * <b>Usage:</b>
 * <br/>
 * <b>1.</b> Specify the filter name in the web.xml
 * <pre>
 * {@code
 * <web-app>
 *     .....
 *     <filter>
 *         <filter-name>TracingWebFilter</filter-name>
 *         <filter-class>com.sc.hm.otl.web.filter.TracingWebFilter</filter-class>
 *         <load-on-startup>1</load-on-startup>
 *     </filter>
 *     .....
 *     .....
 * </web-app>
 * }
 * </pre>
 *
 * If no DECORATOR or SKIP_PATTERN is defined, the FIlter will use the platform
 * provided decorator {@code FilterSpanDecorator} and a {@code null} skip pattern.
 *
 * Optionally one can specify the decorator and the skip pattern
 * as a filter init parameters. See below:
 * 
 * <pre>
 * {@code
 * <web-app>
 *     .....
 *     <filter>
 *         <filter-name>TracingWebFilter</filter-name>
 *         <filter-class>com.sc.hm.otl.web.filter.TracingWebFilter</filter-class>
 *         <load-on-startup>1</load-on-startup>
 *
 *         <init-param>
 *              <param-name>DECORATOR</param-name>
 *              <param-value>fully_qualified_class_name_of_custom_decorator</param-value>
 *          </init-param>
 *          <init-param>
 *              <param-name>SKIP_PATTERN</param-name>
 *              <param-value>pattern_regular_exp</param-value>
 *          </init-param>
 *     </filter>
 *     .....
 *     .....
 * </web-app>
 * }
 * </pre>
 * 
 * You must also specify the url pattern for this filter.
 * <pre>
 * {@code
 * <web-app>
 *     .....
 *     <filter-mapping>
 *         <filter-name>TracingWebFilter</filter-name>
 *         <url-pattern>url_pattern</url-pattern>
 *     </filter-mapping>
 *     .....
 * </web-app>
 * }
 * </pre>
 *
 * This will enable container to invoke the {@link TracingWebFilter} only when the requested
 * url or any of it's child resources is hit. If no url pattern is provided, then
 * the filter will never be invoked.
 * 
 * Note that the client can not provide a custom {@code Tracer} object.
 * It is the platform provided Tracer that will be used everywhere.
 * Also one has to be careful while designing their own decorator. Ensure
 * that it implements {@code FilterSpanDecorator}. This decorator
 * will be added along with other platform provided Decorator(s).
 *
 * <p>
 * <br/>
 * <b>2.</b> Second option is to dynamically register this Filter with the context.
 * The container managed objects (e.g., Filter, Servlet, Listener, etc) are
 * invoked in the following order:
 *   <li>ServletContextListener.contextInitialized</li>
 *   <li>Filter.init</li>
 *   <li>Servlet.init</li>
 *
 * <p>
 * You can add your code in the ServletContextListener.contextInitialized
 * method to programmatically register the tracing filter.
 * 
 * <pre>
 * {@code
 * @Override
 * public void contextInitialized(ServletContextEvent ctxEvent) {
 *     ServletContext context = ctxEvent.getServletContext();
 *
 *     FilterRegistration.Dynamic filterReg = context
 *         .addFilter("OpenTracingFilter", "com.sc.hm.otl.web.filter.TracingWebFilter");
 *
 *     filterReg.setInitParameter(DECORATOR, "fully_qualified_class_name_of_custom_decorator");
 *     filterReg.setInitParameter(SKIP_PATTERN, "<pattern_regular_exp>");
 *     filterReg.addMappingForUrlPatterns(
 *         EnumSet.of(DispatcherType.REQUEST)
 *         , Boolean.TRUE
 *         , "<url_pattern>");
 *
 *     .....
 *     .....
 * }
 * }
 * </pre>
 *
 * The above code will register the TracingWebFilter with the container.
 * Note that it is not mandatory to provide a custom decorator or skip pattern.
 *
 * @author Sudiptasish Chanda
 */
@WebFilter
public class TracingWebFilter implements Filter {
    
    private final Logger logger = LoggerFactory.getLogger(TracingWebFilter.class);
    
    private final Tracer tracer = GlobalTracer.get();
    private final List<RequestCtxDecorator> decorators = new ArrayList<>();
    private final List<Pattern> skipPatterns = new ArrayList<>();
    
    /**
     * Zero argument constructor.
     * 
     * All Filters must have a zero argument constructor. When deployed in a web server
     * the container invokes this constructor while instantiating the Filter.
     * Later it calls the lifecycle API {@link #init(FilterConfig)} to perform any
     * initialization. If there is no zero-arg constructor, then the instantiation
     * will fail.
     */
    public TracingWebFilter() {
    }
    
    @Override
    public void init(FilterConfig config) throws ServletException {
        // Add the standard filter decorators.
        decorators.add(new FilterSpanDecorator());

        // Check if a custom decorator is provided.
        String decoratorClass = config.getInitParameter(OTLConstants.DECORATOR);
        if (decoratorClass != null && (decoratorClass = decoratorClass.trim()).length() > 0) {
            // Initialize and add the decorator.
            decorators.add(initDecorator(decoratorClass));
        }

        // Check the presence of a skip pattern.
        String skipPattern = config.getInitParameter(OTLConstants.SKIP_PATTERN);
        if (skipPattern != null && (skipPattern = skipPattern.trim()).length() > 0) {
            String[] patterns = skipPattern.split(",");
            for (String pattern : patterns) {
                skipPatterns.add(Pattern.compile(pattern.trim()));
            }
        }

        ServletContext ctx = config.getServletContext();
        ctx.setAttribute("otl.filter.decorator", decorators);
        ctx.setAttribute("otl.skip.pattern", skipPatterns);
        
        if (logger.isTraceEnabled()) {
            logger.trace("Initialized the TracingWebFilter with"
                + " Tracer: [{}], Span Decorator: {} and Skip Pattern: [{}]"
                , tracer
                , decorators
                , skipPatterns);
        }
    }

    @Override
    public void doFilter(ServletRequest request
        , ServletResponse response
        , FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        
        if (!isTraceable(httpRequest, httpResponse)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Request with uri [{}] is not traceable", httpRequest.getRequestURI());
            }
            chain.doFilter(request, response);
            return;
        }
        // Now start tracing the request.
        // Create a new span after extracting the span context from the request.
        SpanContext context = tracer.extract(Format.Builtin.TEXT_MAP_EXTRACT
            , new HttpHeaderCarrier(httpRequest));
        
        // Create the span.
        // Creation of span does not necessarily create the scope. Scope indicates
        // the work being done by the current thread at any given point of time.
        // And if that work is to represented by a span, then the span should be
        // explicitly set in the current scope before executing the task.
        Span span = tracer.buildSpan(httpRequest.getMethod())
            .asChildOf(context)
            .start();
        
        if (logger.isTraceEnabled()) {
            logger.trace("Tracing Servlet filter created new Span: {}", span.context());
        }
        
        for (RequestCtxDecorator decorator : decorators) {
            decorator.onRequest(httpRequest, span);
        }
        // Call the next filter or servlet on the request chain.
        // Once the control comes back to this filter, the scope will be closed.
        // As expected the corresponding span won't be finished. Developer has to
        // explicitly call span.finish() to complete the span.
        try (Scope scope = tracer.activateSpan(span)) {
            chain.doFilter(request, response);
            
            for (RequestCtxDecorator decorator : decorators) {
                decorator.onResponse(httpRequest, httpResponse, span);
            }
        }
        catch (Exception e) {
            if (logger.isTraceEnabled()) {
                logger.trace("Error occured while executing span: {}. Error Msg: {}"
                    , span.context()
                    , e.getMessage());
            }
            for (RequestCtxDecorator decorator : decorators) {
                decorator.onError(httpRequest, httpResponse, e, span);
            }
        }
        finally {
            span.finish();
            if (logger.isTraceEnabled()) {
                logger.trace("Finished the Span: {}", span.context());
            }
        }
    }
    
    @Override
    public void destroy() {
        if (logger.isTraceEnabled()) {
            logger.trace("TracingWebFilter is destroyed");
        }
    }

    /**
     * Create and initialize the custom span decorator. This span decorator will
     * be added after {@code StandardFilterSpanDecorator} and
     * platform provided decorator {@code FilterSpanDecorator}.
     *
     * @param decoratorClass Custom decorator class.
     * @return FilterSpanDecorator
     */
    private RequestCtxDecorator initDecorator(String decoratorClass) {
        return ObjectCreator.create(decoratorClass);
    }

    /**
     * Check if the current URL path needs to be traced.
     * 
     * @param request
     * @param response
     * @return boolean
     */
    private boolean isTraceable(HttpServletRequest request, HttpServletResponse response) {
        if (!skipPatterns.isEmpty()) {
            boolean skipped = false;
            
            String contextPath = request.getContextPath();
            String uri = request.getRequestURI().substring(contextPath.length());
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
