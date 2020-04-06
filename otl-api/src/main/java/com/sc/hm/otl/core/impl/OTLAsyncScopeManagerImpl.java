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
