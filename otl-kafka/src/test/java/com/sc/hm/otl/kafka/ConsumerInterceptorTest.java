/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.kafka;

import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSpanVisitor;
import com.sc.hm.otl.core.OTLSyncScopeManager;
import com.sc.hm.otl.util.OTLConstants;
import com.sc.hm.otl.util.TracingUtility;
import io.opentracing.tag.Tags;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class ConsumerInterceptorTest extends AbstractUnitTest {
    
    @Test
    public void testOnConsume() {
        String topic = "test.topic";
        int partition = 1;
        int offset = 13;
        long timestamp = System.currentTimeMillis();
        
        String traceId = "";
        String spanId = "";
        
        Headers headers = new RecordHeaders();
        headers.add(OTLConstants.TRACE_ID_HEADER, (traceId = TracingUtility.newTraceId()).getBytes());
        headers.add(OTLConstants.SPAN_ID_HEADER, (spanId = TracingUtility.newSpanId()).getBytes());
        headers.add(OTLConstants.SAMPLED_HEADER, "0".getBytes());
        
        ConsumerRecord<String, String> record = new ConsumerRecord(topic,
                          partition,
                          offset,
                          timestamp,
                          null,
                          0L,
                          8,
                          26,
                          "test.key",
                          "Hello Consumer Interceptor",
                          headers);
        
        Map<TopicPartition, List<ConsumerRecord<String, String>>> records = new HashMap<>();
        records.put(new TopicPartition(topic, partition), Arrays.asList(record));
        
        ConsumerInterceptor interceptor = new TracingConsumerInterceptor();
        interceptor.onConsume(new ConsumerRecords(records));
        
        OTLSpanVisitor visitor = extract();
        if (visitor instanceof MockSpanVisitor) {
            List<OTLSpan> spans = ((MockSpanVisitor)visitor).getSpans();

            assertEquals(1, spans.size(), "Must have one span created");
            OTLSpan span = spans.get(0);        // This is the kafka consumer interceptor span
            
            assertEquals(traceId, span.context().toTraceId());
            assertEquals(spanId, span.parentSpanId());
            assertEquals("kafka-recieve", span.operation(), "Operation must be " + "kafka-recieve");
            assertTrue(span.startTime() > 0, "Span Start time must be greater than 0");
            assertTrue(span.endTime() > span.startTime(), "Span End time must be greater than Start time");
            
            Map<String, Object> tags = span.tags();

            assertEquals(5, tags.size()
                , "Total number of tags set by StandardKafkaConsumerSpanDecorator.onReceive() must be 5");

            // Now start checking individual tags.
            Object val = tags.get(Tags.COMPONENT.getKey());
            assertEquals(StandardKafkaSpanDecorator.COMPONENT_KAFKA_CONSUMER, val.toString()
                , "Value of tag ["
                    + Tags.COMPONENT.getKey()
                    + "] must be "
                    + StandardKafkaSpanDecorator.COMPONENT_KAFKA_CONSUMER);

            val = tags.get(Tags.MESSAGE_BUS_DESTINATION.getKey());
            assertEquals(topic, val.toString()
                , "Value of tag ["
                    + Tags.MESSAGE_BUS_DESTINATION.getKey()
                    + "] must be "
                    + topic);

            val = tags.get(Tags.SPAN_KIND.getKey());
            assertEquals(Tags.SPAN_KIND_CONSUMER, val.toString()
                , "Value of tag ["
                    + Tags.SPAN_KIND.getKey()
                    + "] must be "
                    + Tags.SPAN_KIND_CONSUMER);

            val = tags.get("partition");
            assertEquals(String.valueOf(partition), val.toString()
                , "Value of tag ["
                    + "partition"
                    + "] must be "
                    + partition);

            val = tags.get("offset");
            assertEquals(String.valueOf(offset), val.toString()
                , "Value of tag ["
                    + "offset"
                    + "] must be "
                    + offset);
            
            spans.clear();
            
            assertTrue(((OTLSyncScopeManager)tracer.scopeManager()).active() == null, "No scope should be present");
            assertTrue(tracer.activeSpan() == null, "No active span should be present");
        }
        else {
            Assertions.fail("Span Visitor must be of type MockSpanVisitor");
        }
    }
    
    @Test
    public void testNoSpanOnConsume() {
        String topic = "test.topic";
        int partition = 1;
        int offset = 13;
        long timestamp = System.currentTimeMillis();
        
        String traceId = "";
        String spanId = "";
        
        Headers headers = new RecordHeaders();
        ConsumerRecord<String, String> record = new ConsumerRecord(topic,
                          partition,
                          offset,
                          timestamp,
                          null,
                          0L,
                          8,
                          26,
                          "test.key",
                          "Hello Consumer Interceptor",
                          headers);
        
        Map<TopicPartition, List<ConsumerRecord<String, String>>> records = new HashMap<>();
        records.put(new TopicPartition(topic, partition), Arrays.asList(record));
        
        ConsumerInterceptor interceptor = new TracingConsumerInterceptor();
        interceptor.onConsume(new ConsumerRecords(records));
        
        OTLSpanVisitor visitor = extract();
        if (visitor instanceof MockSpanVisitor) {
            List<OTLSpan> spans = ((MockSpanVisitor)visitor).getSpans();
            assertEquals(0, spans.size(), "No Span should be created");
        }
        else {
            Assertions.fail("Span Visitor must be of type MockSpanVisitor");
        }
    }
}
