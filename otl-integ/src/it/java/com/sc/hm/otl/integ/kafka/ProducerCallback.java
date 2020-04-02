package com.sc.hm.otl.integ.kafka;

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
