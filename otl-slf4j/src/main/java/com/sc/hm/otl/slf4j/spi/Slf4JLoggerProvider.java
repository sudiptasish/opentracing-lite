/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.slf4j.spi;

import com.sc.hm.otl.core.log.LoggerAdapter;
import com.sc.hm.otl.core.log.LoggerProvider;

/**
 * An Slf4J compliant logger provider.
 *
 * @author Sudiptasish Chanda
 */
public class Slf4JLoggerProvider implements LoggerProvider {

    @Override
    public LoggerAdapter create() {
        return new Slf4JLoggerAdapter();
    }
    
}
