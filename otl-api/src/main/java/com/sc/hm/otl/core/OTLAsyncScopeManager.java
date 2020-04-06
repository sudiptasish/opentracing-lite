/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import io.opentracing.ScopeManager;

/**
 * The OTL async scope manager interface that extend the opentracing {@link ScopeManager}.
 * 
 * This scope manager is a async scope manager, and can be used while performing
 * any asynchronous task. An OTL Tracer can have both sync and async scope manager.
 * Async scope manager deals with {@link OTLAsyncScope}. Unlike {@link OTLSyncScopeManager}
 * it does not rely on thread local to store the scope.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLAsyncScopeManager extends ScopeManager {
}
