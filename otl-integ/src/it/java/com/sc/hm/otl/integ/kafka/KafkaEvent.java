package com.sc.hm.otl.integ.kafka;

import java.util.Map;

/**
 * Wrapper for Kafka messages.
 */
public class KafkaEvent implements IEvent {

    private final String topic;
    private final Integer partition;
    private final Long offset;

    private final Map<String, String> headerMapping;
    private final String key;
    private final byte[] data;

    public KafkaEvent(String topic, Integer partition, Long offset, Map<String, String> headerMapping, String key, byte[] data) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.headerMapping = headerMapping;
        this.key = key;
        this.data = data;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public Integer getPartition() {
        return partition;
    }

    @Override
    public Long getOffset() {
        return offset;
    }

    @Override
    public String getHeader(String key) {
        return headerMapping.get(key);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("Topic: %s, Partition: %d, Offset: %d, Data Size: %d"
                , topic
                , partition
                , offset
                , data.length);
    }
}
