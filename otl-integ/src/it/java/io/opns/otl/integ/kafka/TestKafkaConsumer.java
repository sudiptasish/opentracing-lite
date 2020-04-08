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

import java.io.File;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generic Kafka Consumer utility class. This class provides a simple interface
 * for subscribing to Kafka messages, and encapsulates best practices.
 * <p>
 * An instance of this class has the following life cycle, driven by the client:
 * <p><ul>
 * <li> Client invokes constructor, passing required parameters.
 * <li> Client invokes configure*() methods, if needed, to override default
 *      configuration parameters.
 * <li> Client submits the instance as a task to an executor service.
 * <li> Client invokes stop() method to halt processing.
 * <li> Client invokes shutdownNow() on the executor, to interrupt processing.
 * </ul><p>
 *
 */
public class TestKafkaConsumer implements Runnable {

    private final static Logger logger = LogManager.getLogger(TestKafkaConsumer.class);

    private static final AtomicInteger counter = new AtomicInteger(0);

    // In recent kafka library, the polling and heartbeat are
    // decoupled. This configuration is for the main processing
    // thread. If the current thread, i.e., the consumer does not
    // call the poll(Duration) API after the maxPollTimeMillis is
    // elapsed, then the processing thread may be considered as dead.
    // Depending on how much time the processing takes for maxBatchSize
    // number of events, one should set this value.
    private static final int MAX_POLL_TIME = 5 * 60 * 1000; // 5 minutes

    private static final int SESSION_TIME_OUT = 10 * 1000;  // 10 seconds

    private static final int REQUEST_TIME_OUT = 5 * 60 * 1000 + 5000; // 5 minutes 5 seconds

    // Default values for configurable properties.
    // Total number of messages to be accumulated before it is
    // handed over to event handler.
    private static final int MAX_BATCH_SIZE = 500;

    // Unique If of this consumer.
    private String id = "";

    // Internal kafka consumer.
    private Consumer<String, byte[]> consumer;

    // Total time to wait in anticipation that more number of
    // messages will be received, before the underlying event
    // handler is called.
    // Invocation of the event handler depends on these two parameters.
    // If the max wait time has elapsed, then whatever messages have
    // been gathered, will be sent to the event handler.
    private long maxBatchTimeMillis = 5000L;    // 5 seconds

    private final Properties config = new Properties();

    // Topic name, this consumer is associated with.
    private final String topic;

    private boolean running = false;
    private boolean initialized = false;
    
    // Temporary store where the event will be held.
    private final List<IEvent> eventList = new ArrayList<>(10);

    private TestKafkaConsumer(String topic) {
        this.topic = topic;
        configureDefault();
    }

    /**
     * Return a new instance of a kafka consumer.
     * @param topic
     * @return ECPKafkaConsumer
     */
    public static TestKafkaConsumer newConsumer(String topic) {
        return new TestKafkaConsumer(topic);
    }

    /**
     * Return the unique id of this ECP Kafka Consumer.
     * @return String
     */
    public String getId() {
        return this.id;
    }

    /**
     * Return the topic name this consumer is associated with.
     * @return String
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Create the default configuration for this consumer.
     */
    public void configureDefault() {
        id = "Test-Consumer-" + counter.incrementAndGet();

        config.put(ConsumerConfig.CLIENT_ID_CONFIG, id);

        // Starting Kafka 2.11, consumers are no longer required to
        // connect to the zookeeper for offset management. They can
        // directly connect to the kafka broker instead.
        // Although the offset management is still kept with the
        // zookeeper, but is not transparent to the high level consumer.
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // ecp Group.
        config.put(ConsumerConfig.GROUP_ID_CONFIG, getTopic().replaceAll("\\.", "-") + "-grp");

        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Turn off auto-commit.
        // We will commit the Kafka offsets programmatically
        // (once we are sure that event handling is successful).
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);

        // Use this setting to limit the total records returned from a single
        // call to poll. This can make it easier to predict the maximum that
        // must be handled within each poll interval
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_BATCH_SIZE);

        // By increasing the interval between expected polls, one can give
        // the consumer more time to handle a batch of records returned from
        // the poll(Duration) API. Typically, the poll(Duration) is a blocking
        // call and would wait for the configured time, before returning the
        // result.
        // Set it to a higher value if we want to delay the rebalancing.
        // It's value should be significantly lower than the sessionTimeoutMillis.
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, MAX_POLL_TIME);

        // If the consumer crashes or is unable to send heartbeats for a
        // duration of session.timeout.ms, then the consumer will be
        // considered dead and its partitions will be reassigned.
        // Typically, once a batch of messages are received, the event
        // handler may take some time to process the complete set of
        // messages, however, while the event handler is processing the
        // set of messages, a timeout should not occur. Hence it is advisable
        // to set a higher timeout for the consumer, in order to avoid
        // partition rebalancing, thereby avoid processing duplicate messages.
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, SESSION_TIME_OUT);
        
        config.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIME_OUT);

        config.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.opns.otl.kafka.OTLKafkaConsumerInterceptor");

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
    }

    /**
     * Override the specific configuration property.
     * @param key
     * @param value
     */
    public void overrideConfig(String key, Object value) {
        config.put(key, value);
    }

    /**
     * Override the max batch time config parameter.
     *
     * @param maxBatchTimeMillis
     */
    public void configureMaxBatchTime(long maxBatchTimeMillis) {
        this.maxBatchTimeMillis = maxBatchTimeMillis;
    }

    /**
     * Initialize this kafka consumer.
     *
     * @return boolean
     */
    public boolean configure() {
        this.consumer = new KafkaConsumer<String, byte[]>(config);
        this.consumer.subscribe(Collections.singletonList(getTopic()));

        if (logger.isInfoEnabled()) {
            logger.info("ECP Consumer {} has subscribed to topic {}"
                    , getId()
                    , this.topic);
        }

        this.running = true;
        this.initialized = true;

        if (logger.isInfoEnabled()) {
            logger.info("Initialized ECP Kafka Consumer: " + id);
        }
        return true;
    }

    @Override
    public void run() {
        String orgName = Thread.currentThread().getName();
        try {
            if (!isInitialized()) {
                throw new KafkaException("Kafka Consumer is not Initialized. " +
                        "Configure the consumer before attempting to receive message");
            }
            Thread.currentThread().setName(id);
            internalRun();
        }
        catch (InterruptedException e) {
            logger.error("Kafka Consumer Thread [{}] is interrupted"
                    , Thread.currentThread());
        }
        finally {
            Thread.currentThread().setName(orgName);
        }
    }

    /**
     * Start this Test Kafka Consumer.
     * @throws InterruptedException
     */
    public void internalRun() throws InterruptedException {
        int maxBatchSize = (Integer)config.get(ConsumerConfig.MAX_POLL_RECORDS_CONFIG);
        
        File file = null;
        int counter = 0;
        if (config.getProperty("blocking.file") != null) {
            file = new File(config.getProperty("blocking.file"));
        }

        // Start...
        while (isRunning() && !Thread.currentThread().isInterrupted()) {
            if (file != null && file.exists()) {
                if (counter % 20 == 0) {
                    logger.warn("Blocking file [{}] is present. Remove the file to"
                            + " start consuming message from queue.", file.getAbsolutePath());
                    
                    counter = 0;
                }
                Thread.sleep(5000L);
                
                counter ++;
                continue;
            }
            // Collect a batch of events.
            //while (eventList.size() < maxBatchSize
            //        && timer.currentElapsedTimeMillis() < maxBatchTimeMillis) {

                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000L));   // Max wait time: 1 second.
                if (!records.isEmpty()) {
                    // Prepare the event list.
                    for (ConsumerRecord<String, byte[]> record : records) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Received new kafka message. Topic: {}. "
                                    + "Partition: {}. Offset: {}. Key: {}. Size: {}"
                                    , record.topic()
                                    , record.partition()
                                    , record.partition()
                                    , record.key()
                                    , record.serializedValueSize());
                        }
                        eventList.add(createNew(record));
                    }
                } else {
                    // In case the poll() API returns immediately,
                    // then sleep for soem time before attempting to
                    // read the message again.
                    Thread.sleep(1000L);
                }
            //}
            if (!eventList.isEmpty()) {
                commitOffset();
            }
        }
    }

    /**
     * Commit the offset.
     */
    public void commitOffset() {
        try {
            consumer.commitSync();
            if (logger.isInfoEnabled()) {
                logger.info("Consumer thread [{}] issued a commit", getId());
            }
        }
        catch (CommitFailedException e) {
            // org.apache.kafka.clients.consumer.CommitFailedException:
            // Commit cannot be completed since the group has already
            // rebalanced and assigned the partitions to another member.
            //
            // This means that the time between subsequent calls to poll()
            // was longer than the configured max.poll.interval.ms, which
            // typically implies that the poll loop is spending too much
            // time message processing. You can address this either by
            // increasing max.poll.interval.ms or by reducing the maximum
            // size of batches returned in poll() with max.poll.records.
            logger.error("Commit failed. Reason: ", e);
        }
    }

    private IEvent createNew(ConsumerRecord<String, byte[]> record) {
        Map<String, String> headerMapping = new HashMap<>();

        Headers headers = record.headers();
        if (headers != null) {
            for (Iterator<Header> itr = headers.iterator(); itr.hasNext(); ) {
                Header h = itr.next();
                headerMapping.put(h.key(), new String(h.value()));
            }
        }
        KafkaEvent event = new KafkaEvent(record.topic()
                , record.partition()
                , record.offset()
                , headerMapping
                , record.key()
                , record.value());

        return event;
    }

    /**
     * Check to see if this producer is initialized.
     * @return boolean
     */
    public boolean isInitialized() {
        return initialized;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Notifies the running thread to discontinue processing. Clients
     * should invoke shutdownNow() on the executor after calling this method.
     */
    public void stop() {
        // First disable the main recovery loop.
        running = false;

        // Next, close the connection to zookeeper.
        // This will flush all current writes to zookeeper.
        // It will also clear the stream and then inject  a "shutdown" marker
        // into the stream that will be picked up by the iterator.
        if (consumer != null) {
            consumer.close();
        }
        if (logger.isInfoEnabled()) {
            logger.info("Closed the ECP Kafka Consumer.");
        }
    }

    public List<IEvent> getEventList() {
        return eventList;
    }
}
