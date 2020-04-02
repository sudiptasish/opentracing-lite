/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.metrics.mbean;

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
