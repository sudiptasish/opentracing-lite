/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.slf4j.spi;

import com.sc.hm.otl.core.ctx.ContextAdapter;
import java.util.Map;
import org.slf4j.MDC;

/**
 * Slf4J context adapter implementation.
 * 
 * If the underlying logging system offers MDC functionality, then SLF4J's MDC, 
 * i.e. this class, will delegate to the underlying system's MDC. Note that at 
 * this time, only two logging systems, namely log4j and logback, offer MDC functionality.
 * Whatever contextual info is set by this class, can later be extracted by the 
 * underlying logger framework, with the help <code>%X</code> and dumped to the
 * standard output/file.
 *
 * @author Sudiptasish Chanda
 */
public class Slf4JContextAdapter implements ContextAdapter {
    
    @Override
    public void put(String key, String value) {
        MDC.put(key, value);
    }

    @Override
    public String get(String key) {
        return MDC.get(key);
    }

    @Override
    public void remove(String key) {
        MDC.remove(key);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    @Override
    public void put(Map<String, String> ctxMap) {
        MDC.setContextMap(ctxMap);
    }
}
