/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.metrics.mbean;

/**
 *
 * @author Sudiptasish Chanda
 */
public interface TracerMXBean {
    
    /**
     * Return the scope manager name.
     * @return String
     */
    String getScopeManagerName();
    
    /**
     * Return the name of the propagation registry.
     * @return 
     */
    String getRegistryName();
    
    /**
     * Get the span visitor name.
     * @return 
     */
    String getSpanVisitor();
}
