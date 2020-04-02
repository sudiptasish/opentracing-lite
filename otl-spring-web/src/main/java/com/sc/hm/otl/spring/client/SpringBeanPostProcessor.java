/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.spring.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Custom post processor for any spring bean.
 * 
 * <p>
 * Followings are the steps that takes place during Spring Bean life cycle.
 *
 * <li>Spring loads bean definitions by scanning the classes with the configuration, component annotations.</li>
 * <li>It also load the bean definition by parsing any bean XML files.</li>
 * <li>Bean definitions added to BeanFactory.</li>
 * <li>During the bean creation process, Spring DI will come in to picture to address any dependencies.</li>
 * <li>BeanPostProcessor will allow us to do some additional bean processing before and after bean initialization.</li>
 * <li>The Spring bean is ready to use.</li>
 * 
 * The purpose of this post processor is to capture the instantiation of any {@link RestTemplate}
 * and add the {@link OTLRestTemplateInterceptor} as one of the interceptors to
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
            ((RestTemplate)bean).getInterceptors().add(new RestTemplateInterceptor());
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
