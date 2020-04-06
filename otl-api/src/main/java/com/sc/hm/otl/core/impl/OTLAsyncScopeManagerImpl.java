/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLAsyncScope;
import com.sc.hm.otl.core.OTLAsyncScopeManager;
import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * Implementation of a async scope manager.
 *
 * @author Sudiptasish Chanda
 */
public class OTLAsyncScopeManagerImpl implements OTLAsyncScopeManager {
    
    OTLAsyncScopeManagerImpl() {}

    @Override
    public Scope activate(Span span) {
        OTLAsyncScope asyncScope = new OTLAsyncScopeImpl();
        asyncScope.add(span);
        
        return asyncScope;
    }

    @Override
    public Span activeSpan() {
        return null;
    }
    
}
