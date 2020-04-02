/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * Sync scope is an extension to the opentracing {@link Scope}.
 * SyncScope can only be used with a {@link OTLSyncScopeManager}. As the name
 * suggests it can handle a span whose life cycle is bounded by specific thread,
 * which means, the creator (thread) must take the responsibility of closing the
 * span/scope.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLSyncScope extends Scope {
    
    /**
     * Return the underlying span.
     * For a sync scope, the span must be the currently active span.
     * 
     * @return Span
     */
    Span span();
}
