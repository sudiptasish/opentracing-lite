/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.jaxrs.filter;

import com.sc.hm.otl.util.OTLConstants;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Sudiptasish Chanda
 */
public class RequestHeaderCarrierTest extends AbstractUnitTest {
    
    @Test
    public void testIterator() {
        MultivaluedMap<String, String> props = new MultivaluedHashMap<>();
        props.put(OTLConstants.TRACE_ID_HEADER, Arrays.asList("trc_123"));
        props.put(OTLConstants.SPAN_ID_HEADER, Arrays.asList("spn_123"));
        
        ContainerRequestContext requestCtx = mock(ContainerRequestContext.class);
        when(requestCtx.getHeaders()).thenReturn(props);
        
        RequestHeaderCarrier adapter = new RequestHeaderCarrier(requestCtx);
        int counter = 0;
        
        for (Iterator<Map.Entry<String, String>> itr = adapter.iterator(); itr.hasNext(); counter ++) {
            Map.Entry<String, String> me = itr.next();
            if (OTLConstants.TRACE_ID_HEADER.equals(me.getKey())) {
                assertEquals("trc_123", me.getValue());
            }
            else if (OTLConstants.SPAN_ID_HEADER.equals(me.getKey())) {
                assertEquals("spn_123", me.getValue());
            }
            else {
                fail("Unknown header key [" + me.getKey() + "]");
            }
        }
        assertEquals(props.size(), counter, "Total number headers must be " + props.size());
    }
}
