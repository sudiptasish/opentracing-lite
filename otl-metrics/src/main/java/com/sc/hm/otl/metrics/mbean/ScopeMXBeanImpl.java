/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.metrics.mbean;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Sudiptasish Chanda
 */
public class ScopeMXBeanImpl implements ScopeMXBean {
    
    private AtomicLong activatedScopeCount = new AtomicLong(0);
    private AtomicLong closedScopeCount = new AtomicLong(0);

    @Override
    public Long getTotalScopeActivated() {
        return activatedScopeCount.get();
    }

    @Override
    public Long getTotalScopeClosed() {
        return closedScopeCount.get();
    }

    public void setActivatedScopeCount(Long activatedScopeCount) {
        this.activatedScopeCount.set(activatedScopeCount);
    }

    public void setClosedScopeCount(Long closedScopeCount) {
        this.closedScopeCount.set(closedScopeCount);
    }
    
    public void incrementScopeActivated() {
        this.activatedScopeCount.incrementAndGet();
    }
    
    public void incrementScopeClosed() {
        this.closedScopeCount.incrementAndGet();
    }
}
