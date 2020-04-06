/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.ctx;

import com.sc.hm.otl.util.OTLConstants;
import com.sc.hm.otl.web.filter.TracingWebFilter;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * An alternate way to initialize the {@link TracingWebFilter}.
 * 
 * In a container, <code>web.xml</code> can have a filter, servlet or a listener.
 * Their order of initialization is as follows:
 * 1. {@link ServletContextListener}
 * 2. {@link Filter} or {@link Servlet}
 * 
 * Our aim is to initialize and register the {@link TracingWebFilter} with the container.
 * Sstandard filter registration happens in two ways:
 * 1. Client can decide to specify the filter in the <code>web.xml</code>.
 * 2. Dynamically initialize the filter inside {@link ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent) }.
 * 
 * If you do not want to specify the {@link TracingWebFilter} in the web.xml, then an
 * alternate option is to specify this servlet context listener.
 * 
 * <pre>
 * {@code
 * <web-app>
 *     .....
 *     <listener>
 *         <listener-class>com.sc.hm.otl.web.ctx.TracingContextListener</listener-class>
 *     </listener>
 *     .....
 *     .....
 * </web-app>
 * }
 * </pre>
 * 
 * This will initialize the OTLServletContextListener, which in turn will help
 * register the {@link TracingWebFilter} with default configuration. In default mode, the
 * filter won't have any skip pattern, and use only the platform provided decorator
 * {@link StandardFilterSpanDecorator}.
 * 
 * You can, however, provide a skip pattern and a custom decorator to the filter by
 * specifying them in the <code>context-param</code> tag.
 * 
 * <pre>
 * {@code
 * <web-app>
 *     .....
 *     <context-param>
 *         <param-name>DECORATOR</param-name>
 *         <param-value>fully_qualified_class_name_of_custom_decorator</param-value>
 *     </context-param>
 *     <context-param>
 *         <param-name>SKIP_PATTERN</param-name>
 *         <param-value>pattern_regular_exp</param-value>
 *     </context-param>
 *     .....
 *     <listener>
 *         <listener-class>com.sc.hm.otl.web.ctx.TracingContextListener</listener-class>
 *     </listener>
 *     .....
 *     .....
 * </web-app>
 * }
 * </pre>
 *
 * You must also ensure to provide the url mapping for the {@link TracingWebFilter}, as shown below:
 * 
 * <pre>
 * {@code 
 * <web-app>
 *     <context-param>
 *         <param-name>URL_PATTERN</param-name>
 *         <param-value>url_pattern</param-value>
 *     </context-param>
 * </web-app>
 * }
 * </pre>
 * 
 * This will enable container to invoke the {@link TracingWebFilter} only when the requested
 * url or any of it's child resources is hit. If no url pattern is provided, then
 * the filter will be invoked for every available end point.
 * 
 * @author Sudiptasish Chanda
 */
@WebListener
public class TracingContextListener implements ServletContextListener {
    
    private final Logger logger = LoggerFactory.getLogger(TracingContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent ctxEvent) {
        ServletContext context = ctxEvent.getServletContext();
        
        String decorator = context.getInitParameter(OTLConstants.DECORATOR);
        String skipPattern = context.getInitParameter(OTLConstants.SKIP_PATTERN);
        String urlPattern = context.getInitParameter(OTLConstants.URL_PATTERN);
 
        FilterRegistration.Dynamic filterReg = context
            .addFilter("TracingWebFilter", "com.sc.hm.otl.web.filter.TracingWebFilter");
 
        if (decorator != null) {
            filterReg.setInitParameter(OTLConstants.DECORATOR, decorator);
        }
        if (skipPattern != null) {
            filterReg.setInitParameter(OTLConstants.SKIP_PATTERN, skipPattern);
        }
        if (urlPattern == null || urlPattern.trim().length() == 0) {
            urlPattern = "/*";
        }
        
        filterReg.addMappingForUrlPatterns(
            EnumSet.of(DispatcherType.REQUEST)
            , Boolean.TRUE
            , urlPattern);
 
        if (logger.isTraceEnabled()) {
            logger.trace("Initialized the OTLServletContextListener and registered"
                + " OTLFilter with Span Decorator: [{}] and Skip Pattern: [{}]. URL Pattern: [{}]"
                , decorator
                , skipPattern
                , urlPattern);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent ctxEvent) {
        if (logger.isTraceEnabled()) {
            logger.trace("OTLServletContextListener is destroyed");
        }
    }
}
