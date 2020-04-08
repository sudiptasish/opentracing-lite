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
package io.opns.otl.integ.common;

import io.opns.otl.integ.model.Department;
import io.opns.otl.integ.model.Employee;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sudiptasish Chanda
 */
public class Storage<K, V> {
    
    public static final Storage<String, Employee> EMP_STORAGE = new Storage<>();
    public static final Storage<String, Department> DEPT_STORAGE = new Storage<>();
    
    private final Map<K, V> map = new HashMap<>();
    
    private Storage() {}
    
    public static Storage<String, Employee> getEmpDB() {
        return EMP_STORAGE;
    }
    
    public static Storage<String, Department> getDeptDB() {
        return DEPT_STORAGE;
    }
    
    public void add(K key, V val) {
        map.put(key, val);
    }
    
    public V get(K key) {
        return map.get(key);
    }
    
    public int size() {
        return map.size();
    }
    
    public Collection<V> values() {
        return map.values();
    }
}
