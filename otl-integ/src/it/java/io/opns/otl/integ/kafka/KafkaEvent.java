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
