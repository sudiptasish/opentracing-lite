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
