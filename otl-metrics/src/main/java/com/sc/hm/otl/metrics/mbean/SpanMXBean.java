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
package com.sc.hm.otl.metrics.mbean;

/**
 *
 * @author Sudiptasish Chanda
 */
public interface SpanMXBean {
    
    /**
     * Return the total number of spans created so far.
     * This includes the span that were finished as well as active.
     * 
     * @return Long
     */
    Long getTotalSpans();
    
    /**
     * Get the max time spent to create a span.
     * @return Long
     */
    Long getMaxSpanCreationTime();
    
    /**
     * Get the average span creation time.
     * @return Long
     */
    Long getAvgSpanCreationTime();
    
    /**
     * Return the longest duration that a span took to complete.
     * This API will consider only those spans, for which the {@link Span#finish}
     * method had already been invoked.
     * 
     * @return Long
     */
    Long getMaxSpanDuration();
    
    /**
     * Get the the average duration of a span.
     * @return Long
     */
    Long getAvgSpanDuration();
    
    /**
     * Return the id of the span that took the maximum time to finish.
     * @return String
     */
    String getMaxDurationSpanId();
    
    /**
     * Return the totalnumber of spans that were closed.
     * @return Long
     */
    Long getTotalFinishedSpans();
}
