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
