package com.sc.hm.otl.integ.kafka;

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
