/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.slf4j.spi;

import com.sc.hm.otl.core.ctx.ContextProvider;
import com.sc.hm.otl.core.ctx.ContextAdapter;

/**
 * Provider that helps create a Slf4J compliant context adapter.
 *
 * @author Sudiptasish Chanda
 */
public class Slf4JContextProvider implements ContextProvider {
    
    @Override
    public ContextAdapter create() {
        return new Slf4JContextAdapter();
    }
}
