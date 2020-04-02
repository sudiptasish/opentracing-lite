/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.log;

/**
 * Logger provider interface.
 * 
 * The span data, by default is written to the standard console output. However,
 * while in production, it should be integrated with the underlying logging 
 * framework (if any) to get benefit from it. If there is a provider found, then
 * that provider will be used to bridge the gap between the trace layer and application
 * layer in order to propagate the log to specific destination.
 * otl framework comes with a Slf4J provider. Today the major logging framework,
 * e.g., log4j2, logback, etc follows the SLf4J spec, thus making it easier to
 * seemlessly integrate with the underlying logging framework via an {@link LoggerAdapter}.
 *
 * @author Sudiptasish Chanda
 */
public interface LoggerProvider {
    
    /**
     * API to create a new instance of {@link LoggerAdapter}.
     * @return LoggerAdapter
     */
    LoggerAdapter create();
}
