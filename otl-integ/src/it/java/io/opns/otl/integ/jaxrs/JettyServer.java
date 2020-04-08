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
package io.opns.otl.integ.jaxrs;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
public class JettyServer {
    
    private static final Logger logger = LoggerFactory.getLogger(JettyServer.class);
    
    public static void main(String[] args) throws Exception {
        int port = 7001;
        String ctx = "/";
        
        for (int i = 0; i < args.length - 1; i ++) {
            if (args[i].equals("--port")) {
                port = Integer.parseInt(args[i + 1]);
            }
            else if (args[i].equals("--ctx")) {
                ctx = args[i + 1];
            }
        }
        JettyServer server = new JettyServer();
        DeploymentConfig config = server.getConfig(ctx);
        
        server.startAndDeploy(port, config);
    }
    
    public void startAndDeploy(int port, DeploymentConfig config) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("Initializing Jetty Server");
        }
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath(config.getCotextRoot());
        context.setResourceBase(System.getProperty("java.io.tmpdir"));
        
        Server server = new Server(port);
        server.setHandler(context);

        StringBuilder builder = new StringBuilder(128);
        ServletHolder servletHolder = null;
        List<String> servletNames = config.getServletNames();
        
        for (int i = 0; i < servletNames.size(); i++) {
            servletHolder = new ServletHolder((Class<? extends Servlet>) Class.forName(config.getServlets().get(i)));
            servletHolder.setName(servletNames.get(i));
            servletHolder.setAsyncSupported(Boolean.TRUE);
            
            builder.append("Servlet Name: ").append(servletNames.get(i))
                .append("\nServlet Class: ").append(config.getServlets().get(i));

            Map<String, String> initParams = config.getServletParams().get(servletNames.get(i));
            for (Map.Entry<String, String> me : initParams.entrySet()) {
                servletHolder.setInitParameter(me.getKey(), me.getValue());
                builder.append("\nParam Name: ").append(me.getKey()).append(". Param Value: ").append(me.getValue());
            }
            context.addServlet(servletHolder, config.getServletMapping().get(servletNames.get(i)));
            builder.append("\nServlet Mapping: ").append(config.getServletMapping().get(servletNames.get(i)));
            
            if (logger.isInfoEnabled()) {
                logger.info("Servlet configuration:\n{}", builder.toString());
            }
        }
        FilterHolder filterHolder = null;
        List<String> filterNames = config.getFilterNames();
        
        for (int i = 0; i < filterNames.size(); i++) {
            filterHolder = new FilterHolder((Class<? extends Filter>) Class.forName(config.getFilters().get(i)));
            filterHolder.setName(filterNames.get(i));
            filterHolder.setAsyncSupported(Boolean.TRUE);

            Map<String, String> initParams = config.getServletParams().get(filterNames.get(i));
            for (Map.Entry<String, String> me : initParams.entrySet()) {
                filterHolder.setInitParameter(me.getKey(), me.getValue());
            }
            context.addFilter(filterHolder, config.getServletMapping().get(filterNames.get(i)), EnumSet.of(DispatcherType.REQUEST));
        }
        server.start();
        if (logger.isInfoEnabled()) {
            logger.info("Started Jetty Server. Listening to port: {}.", port);
        }
        server.join();
    }
    
    private DeploymentConfig getConfig(String ctxRoot) {
        DeploymentConfig config = new DeploymentConfig(ctxRoot);
        //config.addFilter("OTLFilter", "io.opns.otl.web.filter.OTLFilter", "/*", new HashMap<>());
        
        Map<String, String> params = new HashMap<>();
        params.put("jersey.config.server.provider.packages", "io.opns.otl.integ,io.opns.otl.jaxrs.filter");
        params.put("com.sun.jersey.spi.container.ContainerRequestFilters", "io.opns.otl.jaxrs.filter.TracingContainerFilter");
        params.put("com.sun.jersey.spi.container.ContainerResponseFilters", "io.opns.otl.jaxrs.filter.TracingContainerFilter");
        params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
        
        config.addServlet("JerseyServlet", "org.glassfish.jersey.servlet.ServletContainer", "/*", params);
        
        return config;
    }
}
