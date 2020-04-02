/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.ctx;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple adapter will store the contextual value in the current threadlocal.
 * It maintains a hashmap in the current thread's threadlocal to store the key, value
 * pair.
 * 
 * @author Sudiptasish Chanda
 */
public class NoOpContextAdapter implements ContextAdapter {
    
    private ThreadLocal<Map<String, String>> ctxLocalMap =
        new ThreadLocal<Map<String, String>>() {
            @Override public Map<String, String> initialValue() {
                return new HashMap<>();
            }
        };

    @Override
    public void put(String key, String value) {
        Map<String, String> tMap = ctxLocalMap.get();
        tMap.put(key, value);
    }

    @Override
    public String get(String key) {
        Map<String, String> tMap = ctxLocalMap.get();
        return tMap.get(key);
    }

    @Override
    public void remove(String key) {
        Map<String, String> tMap = ctxLocalMap.get();
        tMap.remove(key);
    }

    @Override
    public void clear() {
        Map<String, String> tMap = ctxLocalMap.get();
        tMap.clear();
    }

    @Override
    public void put(Map<String, String> ctxMap) {
        // Do Nothing
    }
    
}
