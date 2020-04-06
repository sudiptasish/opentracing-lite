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
