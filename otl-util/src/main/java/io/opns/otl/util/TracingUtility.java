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
