/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.buff;

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
