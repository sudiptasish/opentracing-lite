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
