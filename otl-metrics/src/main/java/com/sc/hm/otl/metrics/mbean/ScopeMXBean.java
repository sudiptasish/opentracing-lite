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
public interface ScopeMXBean {
    
    /**
     * Return the total number of scope activated.
     * @return Long
     */
    Long getTotalScopeActivated();
    
    /**
     * Return the total number of scope closed.
     * @return Long
     */
    Long getTotalScopeClosed();
}
