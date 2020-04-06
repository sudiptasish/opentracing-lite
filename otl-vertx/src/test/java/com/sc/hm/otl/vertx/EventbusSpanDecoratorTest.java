/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.vertx;

import com.sc.hm.otl.core.MessageCtxDecorator;
import com.sc.hm.otl.core.OTLSpan;
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
