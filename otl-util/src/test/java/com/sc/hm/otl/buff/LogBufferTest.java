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
package io.opns.otl.buff;

import io.opns.otl.buff.LogBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class LogBufferTest {
    
    @Test
    public void testAdd() {
        LogBuffer buffer = new LogBuffer(32);
        buffer.add("spanId").add(":").add("abcdef");
        
        String result = buffer.toString();
        assertEquals("spanId:abcdef", result);
        
        buffer.clear();
        
        buffer.add("traceId").add(":").add("1234567890abcd");
        
        result = buffer.toString();
        assertEquals("traceId:1234567890abcd", result);
    }
    
    @Test
    public void testClear() {
        LogBuffer buffer = new LogBuffer(32);
        buffer.add("spanId").add(":").add("abcdef");
        
        String result = buffer.toString();
        assertEquals("spanId:abcdef", result);
        
        buffer.clear();
        assertEquals(0, buffer.length(), "Length of the Buffer must be 0");
        
    }
}
