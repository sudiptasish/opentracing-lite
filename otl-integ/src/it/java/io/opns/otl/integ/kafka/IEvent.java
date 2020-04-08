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

/**
 * Interface that represents a single event.
 */
public interface IEvent {

    /**
     * Return the topic name the message has been arrived from.
     * @return String
     */
    String getTopic();

    /**
     * Return the partition id of the topic this messages
     * has been arrived from.
     *
     * @return Integer
     */
    Integer getPartition();

    /**
     * Return the offset number of the specific partition.
     * @return Integer
     */
    Long getOffset();

    /**
     * Return the partition key name.
     * @return String
     */
    String getKey();

    /**
     * Return header value corresponding to this key.
     * @param key
     * @return String
     */
    String getHeader(String key);

    /**
     * Return the message object.
     * If the message is raw byte array, then a byte array will
     * be returned. Otherwise the custom de-serializer will be
     * called to de-serialize the message before returning the same.
     *
     * @return byte[]
     */
    byte[] getData();
}
