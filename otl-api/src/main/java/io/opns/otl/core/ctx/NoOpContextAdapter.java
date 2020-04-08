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
package io.opns.otl.core.ctx;

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
