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
package io.opns.otl.kafka;

import io.opns.otl.kafka.StandardKafkaSpanDecorator;
import io.opns.otl.kafka.TracingProducerInterceptor;
import io.opns.otl.core.OTLSpan;
import io.opns.otl.core.OTLSpanVisitor;
import io.opns.otl.core.OTLSyncScopeManager;
import io.opns.otl.util.OTLConstants;
import io.opentracing.Scope;
import io.opentracing.tag.Tags;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Sudiptasish Chanda
 */
public class ProducerInterceptorTest extends AbstractUnitTest {
    
    @Test
    public void testOnSendNoParent() {
        String topic = "test.topic";
        
        ProducerInterceptor interceptor = new TracingProducerInterceptor();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "Hello Interceptor");
        
        record = interceptor.onSend(record);
        
        String traceId = "";
        String spanId = "";
        
        OTLSpanVisitor visitor = extract();
        if (visitor instanceof MockSpanVisitor) {
            List<OTLSpan> spans = ((MockSpanVisitor)visitor).getSpans();
            
            assertEquals(1, spans.size(), "Must have one span created");
            OTLSpan span = spans.get(0);
            
            assertNotNull((traceId = span.context().toTraceId()), "TraceId must be non null");
            assertNotNull((spanId = span.context().toSpanId()), "SpanId must be non null");
            assertEquals("kafka-send", span.operation(), "Operation must be " + "kafka-send");
            assertTrue(span.startTime() > 0, "Span Start time must be greater than 0");
            assertTrue(span.endTime() > span.startTime(), "Span End time must be greater than Start time");
            
            Map<String, Object> tags = span.tags();
            
            assertEquals(3, tags.size()
                , "Total number of tags set by StandardKafkaProducerSpanDecorator.onSend() must be 3");

            // Now start checking individual tags.
            Object val = tags.get(Tags.COMPONENT.getKey());
            assertEquals(StandardKafkaSpanDecorator.COMPONENT_KAFKA_PRODUCER, val.toString()
                , "Value of tag ["
                    + Tags.COMPONENT.getKey()
                    + "] must be "
                    + StandardKafkaSpanDecorator.COMPONENT_KAFKA_PRODUCER);

            val = tags.get(Tags.MESSAGE_BUS_DESTINATION.getKey());
            assertEquals(topic, val.toString()
                , "Value of tag ["
                    + Tags.MESSAGE_BUS_DESTINATION.getKey()
                    + "] must be "
                    + topic);

            val = tags.get(Tags.SPAN_KIND.getKey());
            assertEquals(Tags.SPAN_KIND_PRODUCER, val.toString()
                , "Value of tag ["
                    + Tags.SPAN_KIND.getKey()
                    + "] must be "
                    + Tags.SPAN_KIND_PRODUCER);
            
            spans.clear();
            
            assertTrue(((OTLSyncScopeManager)tracer.scopeManager()).active() == null, "No scope should be present");
            assertTrue(tracer.activeSpan() == null, "No active span should be present");
        }
        else {
            Assertions.fail("Span Visitor must be of type MockSpanVisitor");
        }
        // Now check the kafka message headers if the context data is present.
        int count = 0;
        for (Iterator<Header> itr = record.headers().iterator(); itr.hasNext(); ) {
            Header header = itr.next();
            if (header.key().equals(OTLConstants.TRACE_ID_HEADER)) {
                assertEquals(traceId, new String(header.value()));
                count ++;
            }
            else if (header.key().equals(OTLConstants.SPAN_ID_HEADER)) {
                assertEquals(spanId, new String(header.value()));
                count ++;
            }
            else if (header.key().equals(OTLConstants.SAMPLED_HEADER)) {
                assertEquals("0", new String(header.value()));
                count ++;
            }
        }
        assertEquals(3, count);
    }
    
    @Test
    public void testOnSendWithParent() {
        String topic = "test.topic";
        
        ProducerInterceptor interceptor = new TracingProducerInterceptor();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "Hello Interceptor");
        
        // Create a regular span first.
        OTLSpan current = (OTLSpan)tracer.buildSpan("test-flow").start();
        try (Scope scope = tracer.activateSpan(current)) {
            record = interceptor.onSend(record);
        }
        finally {
            current.finish();
        }

        String traceId = "";
        String spanId = "";

        OTLSpanVisitor visitor = extract();
        if (visitor instanceof MockSpanVisitor) {
            List<OTLSpan> spans = ((MockSpanVisitor)visitor).getSpans();

            assertEquals(2, spans.size(), "Must have two spans created");
            OTLSpan span = spans.get(0);        // This is the kafka producer interceptor span
            OTLSpan parent = spans.get(1);      // This is the original/current span

            assertEquals(current, parent);
            assertNotNull((traceId = span.context().toTraceId()), "TraceId must be non null");
            assertNotNull((spanId = span.context().toSpanId()), "SpanId must be non null");
            assertEquals("kafka-send", span.operation(), "Operation must be " + "kafka-send");
            assertTrue(span.startTime() > 0, "Span Start time must be greater than 0");
            assertTrue(span.endTime() > span.startTime(), "Span End time must be greater than Start time");
            
            assertTrue(current.startTime() > 0, "Parent Span Start time must be greater than 0");
            assertTrue(current.endTime() > current.startTime(), "Parent Span End time must be greater than Start time");
            assertEquals(current.context().toTraceId(), traceId, "TraceId of both the spans must be same");
            assertTrue(!current.context().toSpanId().equals(span.context().toSpanId()));
            assertEquals(current.context().toSpanId(), span.parentSpanId());

            Map<String, Object> tags = span.tags();

            assertEquals(3, tags.size()
                , "Total number of tags set by StandardKafkaProducerSpanDecorator.onSend() must be 3");

            // Now start checking individual tags.
            Object val = tags.get(Tags.COMPONENT.getKey());
            assertEquals(StandardKafkaSpanDecorator.COMPONENT_KAFKA_PRODUCER, val.toString()
                , "Value of tag ["
                    + Tags.COMPONENT.getKey()
                    + "] must be "
                    + StandardKafkaSpanDecorator.COMPONENT_KAFKA_PRODUCER);

            val = tags.get(Tags.MESSAGE_BUS_DESTINATION.getKey());
            assertEquals(topic, val.toString()
                , "Value of tag ["
                    + Tags.MESSAGE_BUS_DESTINATION.getKey()
                    + "] must be "
                    + topic);

            val = tags.get(Tags.SPAN_KIND.getKey());
            assertEquals(Tags.SPAN_KIND_PRODUCER, val.toString()
                , "Value of tag ["
                    + Tags.SPAN_KIND.getKey()
                    + "] must be "
                    + Tags.SPAN_KIND_PRODUCER);

            spans.clear();

            assertTrue(((OTLSyncScopeManager)tracer.scopeManager()).active() == null, "No scope should be present");
            assertTrue(tracer.activeSpan() == null, "No active span should be present");
        }
        else {
            Assertions.fail("Span Visitor must be of type MockSpanVisitor");
        }
        // Now check the kafka message headers if the context data is present.
        int count = 0;
        for (Iterator<Header> itr = record.headers().iterator(); itr.hasNext(); ) {
            Header header = itr.next();
            if (header.key().equals(OTLConstants.TRACE_ID_HEADER)) {
                assertEquals(traceId, new String(header.value()));
                count ++;
            }
            else if (header.key().equals(OTLConstants.SPAN_ID_HEADER)) {
                assertEquals(spanId, new String(header.value()));
                count ++;
            }
            else if (header.key().equals(OTLConstants.SAMPLED_HEADER)) {
                assertEquals("0", new String(header.value()));
                count ++;
            }
        }
        assertEquals(3, count);
    }
}
