package com.sc.hm.otl.integ.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for publishing messages to Kafka. This is a wrapper around
 * the Producer class from the Kafka client library. It encapsulates the
 * details of how the Producer is configured, including the connection
 * strategy. This class is parameterized by message type and accepts the topic
 * name as a constructor argument, so it can be used with different message
 * types.
 *
 * @param <T>  message class
 */
public class TestKafkaProducer<T> {

    private final static Logger logger = LoggerFactory.getLogger(TestKafkaProducer.class);

    private static final AtomicInteger counter = new AtomicInteger(0);

    // Topic name, this producer is associated with.
    private final String topic;

    // Unique If of this producer.
    private String id = "";

    // Server Id, this producer is associated with
    private String serverId;

    // Internal kafka producer.
    private Producer<String, T> producer = null;

    private ProducerCallback callback = null;

    private boolean initialized = false;

    private TestKafkaProducer(String topic, String serverId) {
        this.topic = topic;
        this.serverId = serverId;
    }

    /**
     * Return a new instance of a kafka producer.
     * @return ECPKafkaProducer
     */
    public static TestKafkaProducer newProducer(String topic, String serverId) {
        return new TestKafkaProducer(topic, serverId);
    }

    /**
     * Return the unique id of this Kafka Producer.
     * @return String
     */
    public String getId() {
        return this.id;
    }

    /**
     * Create the default configuration for this producer.
     * @return Properties
     */
    public Properties getDefaultSetting() {
        Properties props = new Properties();

        // Bootstrap server configuration.
        // This is nothing but the kafka broker. Multiple brokers
        // can be added separated by comma(,).
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Kafka Producer Id.
        props.put(ProducerConfig.CLIENT_ID_CONFIG, (id = "Test-Producer-" + counter.incrementAndGet()));

        // The acks=0 is none meaning the Producer does not wait for any ack
        // from Kafka broker at all. The records added to the socket buffer
        // are considered sent. There are no guarantees of durability.
        // There could be use cases that need to maximize throughput over
        // durability, for example, log aggregation, in those cases set it to 0.
        //
        // The acks=1 is leader acknowledgment. The means that the Kafka broker
        // acknowledges that the partition leader wrote the record to its local
        // log but responds without the partition followers confirming the write.
        // If leader fails right after sending ack, the record could be lost as
        // the followers might not have replicated the record yet.
        //
        // The acks=all or acks=-1 is all acknowledgment which means the leader
        // gets write confirmation from the full set of ISRs before sending an
        // ack back to the producer. This guarantees that a record is not lost
        // as long as one ISR remains alive. This ack=all setting is the strongest
        // available guarantee that Kafka provides for durability.
        props.put(ProducerConfig.ACKS_CONFIG, "-1");

        // The producer config property linger.ms defaults to 0. One can set this
        // so that the Producer will wait this long before sending if batch size
        // not exceeded. This setting allows the Producer to group together any
        // records that arrive before they can be sent into a batch. Setting this
        // value to 5 ms is greater is good if records arrive faster than they
        // can be sent out.
        props.put(ProducerConfig.LINGER_MS_CONFIG, 6000);        // 6 s

        // The producer config property batch.size defaults to 16 KB.
        // This is used by the Producer to batch records. This setting enables
        // fewer requests and allows multiple records to be sent to the same partition.
        // The smaller the batch size the less the throughput and performance
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 256 * 1024); // 256 KB

        props.put(ProducerConfig.RETRIES_CONFIG, 0);

        // You can also set the producer config property buffer.memory which default
        // 32 MB of memory. This denotes the total memory (in bytes) that the
        // producer can use to buffer records to be sent to the broker.
        // The Producer blocks up to max.block.ms if buffer.memory is exceeded.
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 16 * 1024 * 1024);   // 32 MB
        
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5 * 1000);            // 5 seconds

        // This is a limit to send the larger message.
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1 * 1024 * 1024);  // 1 MB

        // Compression algorithm used by this producer.
        //props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");    // or gzip
        
        // Producer Idempotency.
        //props.put("enable.idempotence", "true");
        //props.put("transactional.id", "prod-1");

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");

        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "com.sc.hm.otl.kafka.OTLKafkaProducerInterceptor");

        return props;
    }

    /**
     * Initialize this kafka producer.
     * @return boolean
     */
    public boolean configure() {
        Properties props = getDefaultSetting();
        return configure(props);
    }

    /**
     * Initialize this kafka producer.
     * Caller may pass a complete set of properties
     * to configure this producer.
     *
     * @param props
     * @return boolean
     */
    public boolean configure(Properties props) {
        this.producer = new KafkaProducer<String, T>(props);
        this.callback = new ProducerCallback(this);

        this.initialized = true;

        if (logger.isInfoEnabled()) {
            logger.info("Initialized Test Kafka Producer: {} for Topic {} running from server {}"
                    , getId()
                    , getTopic()
                    , serverId);
        }
        return true;
    }

    /**
     * Wrapper around the Kafka send method. The native Kafka method will
     * swallow the IO exception thrown by the serializer. This wrapper
     * exposes the exception.
     *
     * @param msgs
     * @throws IOException
     */
    public void send(List<T> msgs) throws IOException {
        send(msgs, null, null);
    }

    /**
     * Wrapper around the Kafka send method.The native Kafka method will
     * swallow the IO exception thrown by the serializer.This wrapper
     * exposes the exception.
     *
     * @param msgs
     * @param headerKeys
     * @param headerValues
     * @throws IOException
     */
    public void send(List<T> msgs
            , String[] headerKeys
            , String[] headerValues) throws IOException {

        if (!isInitialized()) {
            throw new KafkaException("Kafka Producer is not Initialized. " +
                    "Configure the producer before attempting to send message");
        }
        ProducerRecord<String, T> record = null;
        for (int i = 0; i < msgs.size(); i ++) {
            record = new ProducerRecord<>(topic, msgs.get(i));
            if (headerKeys != null) {
                for (byte j = 0; j < headerKeys.length; j ++) {
                    if (headerKeys[j] != null && headerValues[j] != null) {
                        record.headers().add(new RecordHeader(
                                headerKeys[j]
                                , headerValues[j].getBytes()));
                    }
                }
            }
            producer.send(record, callback);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Added {} kafka message(s) to be sent later", msgs.size());
        }
    }

    /**
     * Close the producer pool connections to all Kafka brokers.
     */
    public void close() {
        if (producer != null) {
            producer.close();
            callback = null;
            producer = null;
            initialized = false;
            id = "";

            if (logger.isInfoEnabled()) {
                logger.info("Closed ECP Kafka Producer. Id: {}", getId());
            }
        }
    }

    /**
     * Return the topic name this producer is associated with.
     * @return String
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Check to see if this producer is initialized.
     * @return boolean
     */
    public boolean isInitialized() {
        return initialized;
    }
}
