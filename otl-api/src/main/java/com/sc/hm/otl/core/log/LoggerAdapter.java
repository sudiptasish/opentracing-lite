/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.log;

/**
 * A Span Logger.
 * 
 * Often user wants to dump to useful information about a span or an event while
 * a specific span is active. The {@link LoggerAdapter} helps dumping the information
 * to a specific destination as specified.
 * There can be different implementation of span logger, which sends events/messages
 * to it's own destination.
 *
 * @author Sudiptasish Chanda
 */
public interface LoggerAdapter {
    
    /**
     * Log the message to appropriate destination.
     * The logging will always be done at INFO level.
     * 
     * @param msg 
     */
    void log(String msg);
}
