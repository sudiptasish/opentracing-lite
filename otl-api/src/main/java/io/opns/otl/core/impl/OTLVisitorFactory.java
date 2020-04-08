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

import io.opns.otl.core.OTLSpanVisitor;
import io.opns.otl.core.VisitorFactory;
import io.opns.otl.util.ObjectCreator;

/**
 *
 * @author Sudiptasish Chanda
 */
public class OTLVisitorFactory extends VisitorFactory {
    
    public OTLVisitorFactory() {
        super();
    }
    
    @Override
    public OTLSpanVisitor getVisitor() {
        OTLSpanVisitor visitor;
        
        String visitorClass = System.getProperty("span.visitor");
        if (visitorClass != null && (visitorClass = visitorClass.trim()).length() > 0) {
            visitor = ObjectCreator.create(visitorClass);
        }
        else {
            visitor = new OTLSpanVisitorImpl();
        }
        return visitor;
    }
}
