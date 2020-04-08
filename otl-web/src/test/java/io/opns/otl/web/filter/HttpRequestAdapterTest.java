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
package io.opns.otl.web.filter;

import io.opns.otl.web.filter.HttpHeaderCarrier;
import io.opns.otl.util.OTLConstants;
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
public class HttpRequestAdapterTest extends AbstractUnitTest {
    
    @Test
    public void testIterator() {
        Map<String, String> props = new HashMap<>();
        props.put(OTLConstants.TRACE_ID_HEADER, "trc_123");
        props.put(OTLConstants.SPAN_ID_HEADER, "spn_123");
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(props.keySet()));
        when(request.getHeader(OTLConstants.TRACE_ID_HEADER)).thenReturn(props.get(OTLConstants.TRACE_ID_HEADER));
        when(request.getHeader(OTLConstants.SPAN_ID_HEADER)).thenReturn(props.get(OTLConstants.SPAN_ID_HEADER));
        
        HttpHeaderCarrier adapter = new HttpHeaderCarrier(request);
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
