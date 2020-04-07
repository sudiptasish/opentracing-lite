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
package com.sc.hm.otl.core.ctx;

import java.util.Map;

/**
 * An adapter interface in order to allow two unrelated/uncommon interfaces to work together.
 * In other words, it enable the framework to leverage the underlying framework's 
 * certain capabilities, here passing contextual info, thus making two incompatible
 * interfaces compatible without changing their existing code.
 * 
 * The platform provider must define their own Adapter in order to propagate the
 * call to underlying layer.
 *
 * @author Sudiptasish Chanda
 */
public interface ContextAdapter {
    
    /**
     * Set the specified key, value pair in the current thread's context.
     * 
     * @param key   The key
     * @param value Corresponding value
     */
    void put(String key, String value);
    
    /**
     * Replace the current thread context with the new map.
     * @param ctxMap The context map to be placed in the context.
     */
    void put(Map<String, String> ctxMap);
    
    /**
     * Return the context value corresponding to the key.
     * 
     * @param key   The key whose corresponding value to be retrieved.
     * @return String
     */
    String get(String key);
    
    /**
     * Remove the context data associated with this key.
     * 
     * @param key context key.
     */
    void remove(String key);
    
    /**
     * Clear the current thread context.
     */
    void clear();
}
