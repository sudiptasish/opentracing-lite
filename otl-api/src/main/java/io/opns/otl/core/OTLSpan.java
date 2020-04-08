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
package io.opns.otl.core;

import io.opentracing.Span;
import java.util.List;
import java.util.Map;

/**
 * OTL span.
 * It extends the characteristic of opentracing {@link Span} and provides some 
 * additional behavior. 
 *
 * @author Sudiptasish Chanda
 */
public interface OTLSpan extends Span {
    
    /**
     * Return the operation name.
     * It is advisable to provide a unique name to the span. 
     * 
     * @return String
     */
    String operation();
    
    /**
     * Return the references.
     * @return List
     */
    List<OTLReference> references();
    
    /**
     * Convenient method to get the parent span id.
     * @return String
     */
    String parentSpanId();
    
    /**
     * Indicate whether the span activation to be ignored.
     * @return boolean
     */
    boolean ignoreActive();
    
    /**
     * Return the tags.
     * @return Map
     */
    Map<String, Object> tags();
    
    /**
     * Return the callback object
     * @return Object
     */
    Object callback();
    
    /**
     * Return the start time of the span in microseconds.
     * @return long
     */
    long startTime();
    
    /**
     * Return the end time of the span in microseconds.
     * @return long
     */
    long endTime();
}
