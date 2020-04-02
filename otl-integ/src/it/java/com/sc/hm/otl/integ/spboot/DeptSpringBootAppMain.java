/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.spboot;

import com.sc.hm.otl.spboot.OTLSpringBootConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Sudiptasish Chanda
 */
@SpringBootApplication
@Import({OTLSpringBootConfig.class})
@ComponentScan(basePackages = "com.sc.hm.otl.integ")
public class DeptSpringBootAppMain {
    
    public static void main(String[] args) {
        SpringApplication.run(DeptSpringBootAppMain.class, args);
    }
}