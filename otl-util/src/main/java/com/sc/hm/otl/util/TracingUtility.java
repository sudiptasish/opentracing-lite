/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 *
 * @author Sudiptasish Chanda
 */
public final class TracingUtility {
    
    public static final int TRACE_ID_LENGTH = 32; 
    public static final int SPAN_ID_LENGTH = 16; 
    
    private static final List<Character> CHAR_SET = Arrays.asList(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    
    /**
     * Generate a new trace id.
     * @return String
     */
    public static String newTraceId() {
        return ThreadLocalRandom.current()
            .ints(TRACE_ID_LENGTH, 0, CHAR_SET.size())
            .mapToObj(CHAR_SET::get)
            .map(String::valueOf)
            .collect(Collectors.joining());
    }
    
    /**
     * Generate a new span id.
     * @return String
     */
    public static String newSpanId() {
        return ThreadLocalRandom.current()
            .ints(SPAN_ID_LENGTH, 0, CHAR_SET.size())
            .mapToObj(CHAR_SET::get)
            .map(String::valueOf)
            .collect(Collectors.joining());
    }
}
