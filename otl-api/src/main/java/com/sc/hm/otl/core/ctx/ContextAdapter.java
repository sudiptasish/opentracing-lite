/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
     * @param key
     * @param value 
     */
    void put(String key, String value);
    
    /**
     * Replace the current thread context with the new map.
     * @param ctxMap 
     */
    void put(Map<String, String> ctxMap);
    
    /**
     * 
     * @param key
     * @return 
     */
    String get(String key);
    
    /**
     * 
     * 
     * @param key 
     */
    void remove(String key);
    
    /**
     * Clear the current thread context.
     */
    void clear();
}
