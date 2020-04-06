/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.vertx;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ProcessorVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorVerticle.class);

    @Override
    public void start() throws Exception {
        initConsumer();
        
        if (logger.isInfoEnabled()) {
            logger.info("Started Processor verticle");
        }
    }
    
    private void initConsumer() {
        String address = config().getString("bus.address");
        if (address == null) {
            throw new RuntimeException("Missing 'bus.address' property."
                    + " Cannot configure ProcessorVerticle");
        }
        getVertx().eventBus().consumer(address, new MessageConsumer());
        
        if (logger.isInfoEnabled()) {
            logger.info("Initialized event bus consumer. Listening to: {}", address);
        }
    }
}
