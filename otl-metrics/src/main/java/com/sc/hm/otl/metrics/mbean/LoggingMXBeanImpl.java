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
public class LoggingMXBeanImpl implements LoggingMXBean {
    
    private String contextAdapter = "";
    private String loggerFactoryProvider = "";
    private String loggerAdapter = "";

    @Override
    public String getContextAdapter() {
        return contextAdapter;
    }

    @Override
    public String getLoggerFactoryProvider() {
        return loggerFactoryProvider;
    }

    @Override
    public String getLoggerAdapter() {
        return loggerAdapter;
    }

    public void setContextAdapter(String contextAdapter) {
        this.contextAdapter = contextAdapter;
    }

    public void setLoggerFactoryProvider(String loggerFactoryProvider) {
        this.loggerFactoryProvider = loggerFactoryProvider;
    }

    public void setLoggerAdapter(String loggerAdapter) {
        this.loggerAdapter = loggerAdapter;
    }
    
}
