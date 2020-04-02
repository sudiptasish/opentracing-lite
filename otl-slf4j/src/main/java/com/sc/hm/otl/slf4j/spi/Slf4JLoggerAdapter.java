/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.slf4j.spi;

import com.sc.hm.otl.core.log.LoggerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
public class Slf4JLoggerAdapter implements LoggerAdapter {
    
    private final Logger logger = LoggerFactory.getLogger("otl.span.log");

    @Override
    public void log(String msg) {
        if (logger.isInfoEnabled()) {
            logger.info(msg);
        }
    }
    
}
