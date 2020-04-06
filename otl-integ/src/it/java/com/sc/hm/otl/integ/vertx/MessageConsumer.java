/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.vertx;

import com.sc.hm.otl.integ.model.Department;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class MessageConsumer implements Handler<Message<Department>> {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @Override
    public void handle(Message<Department> event) {
        Department request = event.body();
        
        if (logger.isInfoEnabled()) {
            logger.info("Message consumer received event from event bus."
                    + " Source: {}, Reply: {}. Header(s): {}. Event: {}"
                    , event.address()
                    , event.replyAddress()
                    , event.headers()
                    , request);
        }
        // Do some processing, and return sample response.
        JsonObject json = new JsonObject();
        json.put("trackingId", UUID.randomUUID().toString());
        json.put("message", "Department created successfully");
        
        event.reply(json);
        
        if (logger.isInfoEnabled()) {
            logger.info("Message consumer sent response to reply address: " + event.replyAddress());
        }
    }
}
