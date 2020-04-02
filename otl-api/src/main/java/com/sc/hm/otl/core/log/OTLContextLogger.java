/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.log;

import com.sc.hm.otl.util.ObjectCreator;

/**
 * This class hides and serves as a substitute for the underlying logger provider.
 * 
 * <p>
 * This class, will delegate all the calls to the underlying system's logger implementation.
 * It accomplishes the same with the help of {@link LoggerAdapter}. If the
 * associated framework provides an adapter then the same will be loaded by the
 * provider library. If no adapter is found, then the default {@link ConsoleLoggerAdapter}
 * will be used.
 * 
 * <p>
 * Please note that all methods in this class are static.
 * 
 * @author Sudiptasish Chanda
 */
public final class OTLContextLogger {
    
    private static final LoggerAdapter LOGGER_ADAPTER;
    
    static {
        LoggerProvider provider = LoggerProviderFactory.getProvider();
        if (provider != null) {
            LOGGER_ADAPTER = provider.create();
        }
        else {
            String customAdapter = System.getProperty("logger.adapter");
            if (customAdapter != null && (customAdapter = customAdapter.trim()).length() > 0) {
                LOGGER_ADAPTER = ObjectCreator.create(customAdapter);
            }
            else {
                LOGGER_ADAPTER = new ConsoleLoggerAdapter();
            }
        }
    }
    
    /**
     * 
     * @param msg 
     */
    public static void log(String msg) {
        LOGGER_ADAPTER.log(msg);
    }
}
