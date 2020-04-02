/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
