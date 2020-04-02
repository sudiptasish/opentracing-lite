/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.core;

import io.opentracing.ScopeManager;

/**
 * The OTL scope manager interface that extend the opentracing {@link ScopeManager}.
 * This scope manager is a sync scope manager, and can be used while performing
 * any synchronous task. The system property scope.manager.type will determine the
 * scope manager that will be used within a tracer. The default nature of the
 * scopemanager is sync. Because this scope manager supports only synchronous operation,
 * hence it uses the {@link ThreadLocal} to store the current {@link Scope}.
 * A synchronous scope manager will always use a {@link OTLSyncScope} to store the
 * span hierarchy.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLSyncScopeManager extends ScopeManager {
    
    /**
     * Return the currently active scope.
     * @return OTLSyncScope
     */
    OTLSyncScope active();
}
