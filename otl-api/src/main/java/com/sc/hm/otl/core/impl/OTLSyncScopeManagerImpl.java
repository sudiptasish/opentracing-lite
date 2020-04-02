/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core.impl;

import com.sc.hm.otl.core.OTLSpan;
import com.sc.hm.otl.core.OTLSyncScope;
import com.sc.hm.otl.core.OTLSyncScopeManager;
import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * Implementation of a sync scope manager.
 *
 * @author Sudiptasish Chanda
 */
public class OTLSyncScopeManagerImpl implements OTLSyncScopeManager {
    
    // Since sync scope manager supports synchronous operation only, therefore
    // it leverages the thread local to store the scope data.
    final ThreadLocal<OTLSyncScope> tlScope = new ThreadLocal<>();
    
    OTLSyncScopeManagerImpl() {}

    @Override
    public Scope activate(Span span) {
        // Activating a span merely means creating a new scope for this span
        // and store it in the current threadlocal.
        // Calling this method multiple times would not have any impact.
        OTLSyncScope curent = tlScope.get();
        OTLSpan otlSpan = (OTLSpan)span;
        if (curent != null && curent.span() == otlSpan) {
            return curent;
        }
        // If the given span is already set somewhere down in the hierarchy, then
        // it will be pulled to the beginning.
        // TODO
        
        OTLSyncScope newScope = new OTLSyncScopeImpl(this, otlSpan, curent);
        return newScope;
    }

    @Override
    public Span activeSpan() {
        OTLSyncScope current = tlScope.get();
        return current != null ? current.span() : null;
    }

    @Override
    public OTLSyncScope active() {
        return tlScope.get();
    }
    
}
