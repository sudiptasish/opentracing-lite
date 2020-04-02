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
public interface LoggingMXBean {
    
    /**
     * Return the name of the context adapter.
     * @return 
     */
    String getContextAdapter();
    
    /**
     * Return the logger factory provider.
     * @return 
     */
    String getLoggerFactoryProvider();
    
    /**
     * Return the name of the logger adapter.
     * @return 
     */
    String getLoggerAdapter();
}
