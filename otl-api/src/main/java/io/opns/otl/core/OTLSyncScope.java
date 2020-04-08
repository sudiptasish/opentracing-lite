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
package io.opns.otl.core;

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
