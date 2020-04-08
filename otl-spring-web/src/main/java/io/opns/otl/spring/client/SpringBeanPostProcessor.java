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
package io.opns.otl.spring.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Custom post processor for any spring bean.
 * 
 * <p>
 * Followings are the steps that takes place during Spring Bean life cycle.
 *
 * <ul>
 * <li>Spring loads bean definitions by scanning the classes with the configuration, component annotations.</li>
 * <li>It also load the bean definition by parsing any bean XML files.</li>
 * <li>Bean definitions added to BeanFactory.</li>
 * <li>During the bean creation process, Spring DI will come in to picture to address any dependencies.</li>
 * <li>BeanPostProcessor will allow us to do some additional bean processing before and after bean initialization.</li>
 * <li>The Spring bean is ready to use.</li>
 * </ul>
 * 
 * The purpose of this post processor is to capture the instantiation of any {@link RestTemplate}
 * and add the {@link RestClientInterceptor} as one of the interceptors to
 * propaagate the span context.
 * 
 * <p>
 * The easiest way to register the Spring BeanPostProcessor is by annotating the 
 * class with @Component or define the bean in <code>bean.xml</code> file or in 
 * the configuration file.
 *
 * @author Sudiptasish Chanda
 */
//@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(SpringBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (logger.isTraceEnabled()) {
            logger.trace("SpringBeanPostProcessor::postProcessBeforeInitialization invoked for: {}", beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (logger.isTraceEnabled()) {
            logger.trace("SpringBeanPostProcessor::postProcessAfterInitialization invoked for: {}", beanName);
        }
        if (bean instanceof RestTemplate) {
            ((RestTemplate)bean).getInterceptors().add(new RestClientInterceptor());
            if (logger.isTraceEnabled()) {
                logger.trace("Successfully added interceptor [OTLRestTemplateInterceptor] to RestTemplate");
            }
        }
        else if (bean instanceof WebClient) {
            if (logger.isTraceEnabled()) {
                logger.trace("Successfully added interceptor [WebClientInterceptor] to WebClient");
            }
            // Mutate will copy the builder state and create a new WebClient out of it.
            return ((WebClient)bean).mutate()
                .filter(new WebClientInterceptor())
                .build();
        }
        else if (bean instanceof WebClient.Builder) {
            ((WebClient.Builder)bean).filter(new WebClientInterceptor());
            if (logger.isTraceEnabled()) {
                logger.trace("Successfully added interceptor [WebClientInterceptor] to WebClient.Builder");
            }
        }
        return bean;
    }
    
    
}
