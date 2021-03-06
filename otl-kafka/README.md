# Backround

Apache Kafka is a distributed messaging system, much like any conventional messaging system like tibco, jms, etc, but internet scale. In any conventional messaging system, there is a producer, a message queue and a consumer at the other side. So faar we have been discussing about synchronous request flow. Take the same example of Employee service and Department service. Imagine, Employee service, instead of directly calling Department service, sends the message to kafka queue via a producer. Department service, later, picks up the message (event) via it's own consumer and performs the task of department creation.

How do we link these two works (employee creation and department creation) ? The traceId that was originally generated by the client facing Employee service must now be propagated all the way through kafka broker and queue to finally reach Department service. 

This is where the otl-kafka library comes into play. It provides the appropriate interceptor to instrument the message in order to inject the correlationId (traceId) and other header information.

# How to configure

## Modify pom file

Add the following dependency in your pom file:

```
<dependencies>
    ....
    <dependency>
        <groupId>io.opns.otl</groupId>
        <artifactId>otl-web</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <!--  Add the below, only if you want to monitor the span metrics -->
    <dependency>
        <groupId>io.opns.otl</groupId>
        <artifactId>otl-metrics</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ....
</dependencies>
```

## Add the Producer interceptor

Starting version 2.1, kafka provides the feature of message headers. Like jms, one can insert additional header informatin while sending the packet to the remote queue. The custom interceptor leverages this feature to inject the traceId and other contextual info in the header.

```
public Producer createProducer() {
    Producer<String, T> producer;
       
    Properties config = new Properties();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(ProducerConfig.CLIENT_ID_CONFIG, "Producer-1");
    ....
    ....
    config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.opns.otl.kafka.OTLKafkaProducerInterceptor");
    producer = new KafkaProducer<String, T>(config);
    return producer
}

public void send(String topic, T msg) {
    Producer<String, T> producer = createProducer();
    ProducerRecord<String, T> record = new ProducerRecord<>(topic, msg);
    producer.send(record);
         
    producer.close();
}
```

## Add the Consumer interceptor

On the consumer service, add the interceptor for the kafka consumer, which can extract the traceId and all other contextual info from the message header.

```
public Consumer createConsumer() {
    Consumer<String, byte[]> consumer;
         
    Properties config = new Properties();
         
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(ConsumerConfig.CLIENT_ID_CONFIG, "Consumer-1");
         ....
         ....
    config.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG
        , "io.opns.otl.kafka.OTLKafkaConsumerInterceptor");
  
    consumer = new KafkaConsumer<String, byte[]>(config);
    return consumer
}
 
public void receive(String topic) {
    Consumer<String, byte[]> consumer = createConsumer();
    consumer.subscribe(Collections.singletonList(topic));
  
    while (true) {
        ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000L));
        if (!records.isEmpty()) {
            for (ConsumerRecord<String, byte[]> record : records) {
                // process the individual message
            }
        }
        consumer.commitSync();
    }
    // consumer.close();
}

```

Build both the producer (Employee service) and consumer (Department service). Add the java agent in the server startup script.

```
java -javaagent:/path/to/otl-agent.jar ......
```

Start your server. You will notice the tracing headers getting propagated to the consumer service.
