/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.spring.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Sudiptasish Chanda
 */
@Configuration
@ComponentScan(basePackages = "com.sc.hm.otl.spring.client")
public class OTLSpringWebConfig {
    
    /**
     * API to return the custom bean post processor.
     * @return SpringBeanPostProcessor
     */
    @Bean
    public SpringBeanPostProcessor postProcessor() {
        return new SpringBeanPostProcessor();
    }
}
