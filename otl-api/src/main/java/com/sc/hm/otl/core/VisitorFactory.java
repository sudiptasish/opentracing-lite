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

import com.sc.hm.otl.core.impl.OTLVisitorFactory;
import io.opentracing.Tracer;

/**
 * Factory class for instantiating the visitor.
 *
 * @author Sudiptasish Chanda
 */
public abstract class VisitorFactory {
    
    /**
     * Return the platfor visitor factory.
     * @return VisitorFactory
     */
    public static VisitorFactory getFactory() {
        return new OTLVisitorFactory();
    }
    
    /**
     * API to create a new visitor.
     * A visitor object is attached to a {@link Tracer}. Therefore it's lifecycle
     * is managed by the tracer itself. It will stay there as long as the Tracer
     * object is active.
     * 
     * Note that, {@link OTLSpanVisitor} is stateless, that means, it does not
     * maintain any state, which is why it is thread safe.
     * 
     * @return 
     */
    public abstract OTLSpanVisitor getVisitor();
}
