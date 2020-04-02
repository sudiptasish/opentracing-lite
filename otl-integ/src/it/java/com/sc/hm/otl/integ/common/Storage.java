/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.integ.common;

import com.sc.hm.otl.integ.model.Department;
import com.sc.hm.otl.integ.model.Employee;
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
