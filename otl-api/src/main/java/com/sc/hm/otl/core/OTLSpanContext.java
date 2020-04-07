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
package com.sc.hm.otl.core;

import io.opentracing.SpanContext;
import java.util.Map;

/**
 * OTL Span context.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLSpanContext extends SpanContext {
    
    /**
     * Return the parent span id presents in the context.
     * @return String
     */
    //String parentSpanId();
    
    /**
     * Add the baggage item to this span context.
     * 
     * @param key       The baggage key
     * @param value     Corresponding baggage item value
     */
    void addBaggageItem(String key, String value);
    
    /**
     * Return the item value corresponding to this item key.
     * @param key       The baggage key
     * @return String
     */
    String getBaggageItem(String key);
    
    /**
     * Get the baggage item map.
     * @return Map
     */
    Map<String, String> getBaggageItems();
    
    /**
     * Return the sampling value.
     * @return int
     */
    Integer sampled();
}
