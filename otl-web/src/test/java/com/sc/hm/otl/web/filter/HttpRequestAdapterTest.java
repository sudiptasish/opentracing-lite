/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.web.filter;

import com.sc.hm.otl.web.filter.HttpRequestAdapter;
import com.sc.hm.otl.util.OTLConstants;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Sudiptasish Chanda
 */
public class HttpRequestAdapterTest extends AbstractWebTest {
    
    @Test
    public void testIterator() {
        Map<String, String> props = new HashMap<>();
        props.put(OTLConstants.TRACE_ID_HEADER, "trc_123");
        props.put(OTLConstants.SPAN_ID_HEADER, "spn_123");
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(props.keySet()));
        when(request.getHeader(OTLConstants.TRACE_ID_HEADER)).thenReturn(props.get(OTLConstants.TRACE_ID_HEADER));
        when(request.getHeader(OTLConstants.SPAN_ID_HEADER)).thenReturn(props.get(OTLConstants.SPAN_ID_HEADER));
        
        HttpRequestAdapter adapter = new HttpRequestAdapter(request);
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
