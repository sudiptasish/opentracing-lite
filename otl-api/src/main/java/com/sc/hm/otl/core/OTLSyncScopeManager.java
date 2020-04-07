/*
 *     Copyright 2020 Opentracing-LiTE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sc.hm.otl.core;

import io.opentracing.ScopeManager;
import io.opentracing.Scope;

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
