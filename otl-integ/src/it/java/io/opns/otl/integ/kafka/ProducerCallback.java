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
package io.opns.otl.integ.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerCallback implements Callback {

    private final static Logger logger = LoggerFactory.getLogger(ProducerCallback.class);

    // The underlying kafka producer this callback is associated with.
    private final TestKafkaProducer producer;

    public ProducerCallback(TestKafkaProducer producer) {
        this.producer = producer;
    }

    @Override
    public void onCompletion(RecordMetadata rm, Exception e) {
        if (e != null) {
            logger.error("Error occurred while sending kafka message. " +
                    "Affected Producer: " + producer.getId(), e);
        }
        else {
            if (logger.isInfoEnabled()) {
                logger.info("Message Sent to Topic {}, Partition {}, Offset {}. Timestamp: {}"
                        , rm.topic()
                        , rm.partition()
                        , rm.hasOffset() ? rm.offset() : -1
                        , new Date(rm.timestamp()));
            }
        }
    }
}
