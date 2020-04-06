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
package com.sc.hm.otl.buff;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A fixed size buffer cache.
 *
 * @author Sudiptasish Chanda
 */
public class BufferCache {
    
    private static final int IDLE = 0;
    private static final int READING = 1;
    private static final int WRITING = 2;
    
    private static final int DEFAULT_SIZE = 128;
    
    private final int size;
    private final Queue<LogBuffer> buffers = new LinkedList<>();
    
    private final AtomicInteger lock = new AtomicInteger(IDLE);
    
    public BufferCache() {
        this(DEFAULT_SIZE);
    }
    
    public BufferCache(int size) {
        this.size = Integer.parseInt(System.getProperty(
            "buffer.cache.size"
            , String.valueOf(size)));
        
        for (int i = 0; i < this.size; i ++) {
            buffers.offer(new LogBuffer());
        }
    }
    
    public LogBuffer next() {
        try {
            if (buffers.isEmpty()) {
                return null;
            }
            while (!lock.compareAndSet(IDLE, READING));
            
            if (buffers.isEmpty()) {
                return null;
            }
            return buffers.poll();
        }
        finally {
            lock.set(IDLE);
        }
    }
    
    public void release(LogBuffer buffer) {
        try {
            if (buffers.size() == size) {
                throw new IllegalStateException("BufferCache already has " + size + " element(s)");
            }
            while (!lock.compareAndSet(IDLE, WRITING));
            buffer.clear();
            buffers.offer(buffer);
        }
        finally {
            lock.set(IDLE);
        }
    }
    
    public int available() {
        return buffers.size();
    }
}
