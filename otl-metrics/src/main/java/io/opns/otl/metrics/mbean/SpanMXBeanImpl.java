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
package io.opns.otl.metrics.mbean;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Sudiptasish Chanda
 */
public class SpanMXBeanImpl implements SpanMXBean {
    
    private static final boolean IDLE = false;
    private static final boolean ACTIVE = true;
    
    private Long totalSpans = 0L;
    private Long maxSpanCreationTime = 0L;
    private Long avgSpanCreationTime = 0L;
    
    private Long maxSpanDuration = 0L;
    private Long avgSpanDuration = 0L;
    private String maxDurationSpanId = "";
    private Long totalFinishedSpans = 0L;
    
    private final AtomicBoolean cLock = new AtomicBoolean(IDLE);
    private final AtomicBoolean fLock = new AtomicBoolean(IDLE);

    @Override
    public Long getTotalSpans() {
        return totalSpans;
    }

    @Override
    public Long getMaxSpanCreationTime() {
        return maxSpanCreationTime;
    }

    @Override
    public Long getAvgSpanCreationTime() {
        return avgSpanCreationTime;
    }

    @Override
    public Long getMaxSpanDuration() {
        return maxSpanDuration;
    }

    @Override
    public Long getAvgSpanDuration() {
        return avgSpanDuration;
    }

    @Override
    public String getMaxDurationSpanId() {
        return maxDurationSpanId;
    }

    @Override
    public Long getTotalFinishedSpans() {
        return totalFinishedSpans;
    }

    public void setMaxSpanCreationTime(Long maxSpanCreationTime) {
        this.maxSpanCreationTime = maxSpanCreationTime;
    }

    public void setAvgSpanDuration(Long avgSpanDuration) {
        this.avgSpanDuration = avgSpanDuration;
    }

    public void setAvgSpanCreationTime(Long avgSpanCreationTime) {
        this.avgSpanCreationTime = avgSpanCreationTime;
    }

    public void setTotalSpans(Long totalSpans) {
        this.totalSpans = totalSpans;
    }

    public void setMaxSpanDuration(Long maxSpanDuration) {
        this.maxSpanDuration = maxSpanDuration;
    }

    public void setMaxDurationSpanId(String maxDurationSpanId) {
        this.maxDurationSpanId = maxDurationSpanId;
    }

    public void setTotalFinishedSpans(Long totalFinishedSpans) {
        this.totalFinishedSpans = totalFinishedSpans;
    }
    
    public void updateSpanCreation(Long creationTime) {
        try {
            while(cLock.compareAndSet(IDLE, ACTIVE));
            this.totalSpans ++;
            if (creationTime > this.maxSpanCreationTime) {
                this.maxSpanCreationTime = creationTime;
            }
            this.avgSpanCreationTime = (this.avgSpanCreationTime + creationTime) / this.totalSpans;
        }
        finally {
            cLock.set(IDLE);
        }
    }
    
    public void updateSpanFinish(String spanId, Long duration) {
        try {
            while(fLock.compareAndSet(IDLE, ACTIVE));
            if (duration > this.maxSpanDuration) {
                this.maxDurationSpanId = spanId;
                this.maxSpanDuration = duration;
            }
            this.avgSpanDuration = (this.avgSpanDuration + duration) / this.totalSpans;
            this.totalFinishedSpans ++;
        }
        finally {
            fLock.set(IDLE);
        }
    }
}
