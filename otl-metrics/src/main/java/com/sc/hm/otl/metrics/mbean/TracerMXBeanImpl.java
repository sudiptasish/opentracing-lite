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
package com.sc.hm.otl.metrics.mbean;

import com.sc.hm.otl.core.OTLSyncScopeManager;
import com.sc.hm.otl.core.impl.CarrierRegistry;
import com.sc.hm.otl.core.impl.OTLSpanVisitorImpl;

/**
 *
 * @author Sudiptasish Chanda
 */
public class TracerMXBeanImpl implements TracerMXBean {
    
    private String scopeManagerName = OTLSyncScopeManager.class.getSimpleName();
    private String registryName = CarrierRegistry.class.getSimpleName();
    private String spanVisitor = OTLSpanVisitorImpl.class.getSimpleName();

    @Override
    public String getScopeManagerName() {
        return this.scopeManagerName;
    }

    @Override
    public String getRegistryName() {
        return this.registryName;
    }

    @Override
    public String getSpanVisitor() {
        return this.spanVisitor;
    }
    
}
