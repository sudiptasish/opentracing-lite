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
package io.opns.otl.core.impl;

import io.opns.otl.core.OTLSpan;
import io.opns.otl.core.OTLSyncScope;
import io.opns.otl.core.OTLSyncScopeManager;
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
