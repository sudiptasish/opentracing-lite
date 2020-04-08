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
package io.opns.otl.vertx;

import io.opns.otl.vertx.EventbusSpanDecorator;
import io.opns.otl.core.MessageCtxDecorator;
import io.opns.otl.core.OTLSpan;
import io.opentracing.tag.Tags;
import io.vertx.core.eventbus.DeliveryContext;
import io.vertx.core.eventbus.Message;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Sudiptasish Chanda
 */
public class EventbusSpanDecoratorTest extends AbstractUnitTest {
    
    @Test
    public void testOnSend() {
        String address = "destination.test.address";
        
        DeliveryContext context = mock(DeliveryContext.class);
        Message message = mock(Message.class);
        
        when(message.address()).thenReturn(address);
        when(context.message()).thenReturn(message);
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("eventbus_send").start();
        
        MessageCtxDecorator decorator = new EventbusSpanDecorator();
        decorator.onSend(context, span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onRequest method is 3.
        assertEquals(3, tags.size()
            , "Total number of tags set by EventbusSpanDecorator.onSend() must be 3");
        
        // Now start checking individual tags.
        Object val = tags.get(Tags.COMPONENT.getKey());
        assertEquals(EventbusSpanDecorator.SENDER, val.toString()
            , "Value of tag ["
                + Tags.COMPONENT.getKey()
                + "] must be "
                + EventbusSpanDecorator.SENDER);
        
        val = tags.get(Tags.MESSAGE_BUS_DESTINATION.getKey());
        assertEquals(address, val.toString()
            , "Value of tag ["
                + Tags.MESSAGE_BUS_DESTINATION.getKey()
                + "] must be "
                + address);
        
        val = tags.get(Tags.SPAN_KIND.getKey());
        assertEquals(Tags.SPAN_KIND_PRODUCER, val.toString()
            , "Value of tag ["
                + Tags.SPAN_KIND.getKey()
                + "] must be "
                + Tags.SPAN_KIND_PRODUCER);
    }
    
    @Test
    public void testOnReceive() {
        String address = "destination.test.address";
        
        DeliveryContext context = mock(DeliveryContext.class);
        Message message = mock(Message.class);
        
        when(message.address()).thenReturn(address);
        when(context.message()).thenReturn(message);
        
        OTLSpan span = (OTLSpan)tracer.buildSpan("eventbus_send").start();
        
        MessageCtxDecorator decorator = new EventbusSpanDecorator();
        decorator.onReceive(context, span);
        
        Map<String, Object> tags = span.tags();
        
        // Total number of tags set inside onRequest method is 30.
        assertEquals(0, tags.size()
            , "Total number of tags set by EventbusSpanDecorator.onSend() must be 0");
    }
}
