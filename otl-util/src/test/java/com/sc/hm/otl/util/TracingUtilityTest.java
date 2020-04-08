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
package io.opns.otl.util;

import io.opns.otl.util.TracingUtility;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Sudiptasish Chanda
 */
public class TracingUtilityTest {
    
    @Test
    public void testNewTraceId() {
        String traceId = TracingUtility.newTraceId();
        assertEquals(TracingUtility.TRACE_ID_LENGTH, traceId.length()
            , "TraceId must be " + TracingUtility.TRACE_ID_LENGTH + " character length");
    }
    
    @Test
    public void testNewSpanId() {
        String spanId = TracingUtility.newSpanId();
        assertEquals(TracingUtility.SPAN_ID_LENGTH, spanId.length()
            , "SpanId must be " + TracingUtility.SPAN_ID_LENGTH + " character length");
    }
}
