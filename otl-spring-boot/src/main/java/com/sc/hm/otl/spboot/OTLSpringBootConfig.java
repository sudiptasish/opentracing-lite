/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.spboot;

import com.sc.hm.otl.spring.client.SpringWebConfig;
import com.sc.hm.otl.web.filter.TracingWebFilter;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * OTL Spring Boot configuration.
 * 
 * Although the {@link TracingWebFilter} includes the {@link @WebFilter} annotation, and
 * today most of the web server/application server can recognize this annotation
 * and accordingly load the filter. However, spring boot uses the embedded web
 * server (Apache Tomcat). Due to the fact that embedded containers do not support
 * @WebServlet, @WebFilter and @WebListener annotations, Spring Boot, relying greatly
 * on embedded containers, introduced this new annotation @ServletComponentScan to
 * support some dependent jars that use these 3 annotations.
 * 
 * Here we use the {@link ServletComponentScan} annotation as a hint to the embedded
 * web container to facilitate the loading of the OTL filter.
 * 
 * This annotation will scan the specified package to find and load the OTL filter.
 * It will be initialized with default configuration, which includes the default
 * {@link StandardFilterSpanDecorator} and no skip pattern. The url pattern will be
 * set by spring boot, which is [/*], which means this filter will be invoked for
 * all possible urls.
 *
 * @author Sudiptasish Chanda
 */
@Configuration
@ServletComponentScan(basePackages = "com.sc.hm.otl.web.filter")
@Import({SpringWebConfig.class})
public class OTLSpringBootConfig {
    
    // In future, if additional Bean needs to be created, can be added here.
}
