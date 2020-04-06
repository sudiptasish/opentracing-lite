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

import com.sc.hm.otl.core.OTLReference;
import com.sc.hm.otl.core.OTLSpanContext;

/**
 * Platform provided reference object.
 *
 * @author Sudiptasish Chanda
 */
public class OTLReferenceImpl implements OTLReference {
    
    private final String refType;
    private final OTLSpanContext parent;
    
    public OTLReferenceImpl(String refType, OTLSpanContext parent) {
        this.refType = refType;
        this.parent = parent;
    }

    @Override
    public String type() {
        return refType;
    }

    @Override
    public OTLSpanContext context() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OTLReferenceImpl) {
            OTLReferenceImpl other = (OTLReferenceImpl)obj;
            return refType.equals(other.refType) && parent.equals(other.parent);
        }
        return false;
    }
}
