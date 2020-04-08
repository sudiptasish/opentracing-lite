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
package io.opns.otl.buff;

import io.opns.otl.buff.LogBuffer;
import io.opns.otl.buff.BufferCache;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Sudiptasish Chanda
 */
public class BufferCacheTest {
    
    @Test
    public void testBufferOps() throws Exception {
        int CACHE_SIZE = 2;
        int WORKER_COUNT = 3;
        
        CountDownLatch latch = new CountDownLatch(CACHE_SIZE);
        BufferCache cache = new BufferCache(CACHE_SIZE);
        
        Status[] statuses = new Status[WORKER_COUNT];
        
        Thread[] workers = new Thread[WORKER_COUNT];
        for (int i = 0; i < workers.length; i ++) {
            statuses[i] = new Status();
            workers[i] = new Thread(new SpanWorker((i + 1), cache, latch, statuses[i]));
        }
        
        // Start the 3rd worker.
        for (int i = 0; i < workers.length; i ++) {
            workers[i].start();
        }
        
        for (int i = 0; i < workers.length; i ++) {
            workers[i].join();
        }
        Status status_1 = statuses[0];
        Status status_2 = statuses[1];
        Status status_3 = statuses[2];
        
        assertEquals(true, status_1.found, "Worker 1 should find log buffer from cache");
        assertEquals(true, status_2.found, "Worker 2 should find log buffer from cache");
        assertEquals(false, status_3.found, "Worker 3 will fail to get a log buffer from cache");
        
        assertTrue(status_1.error == null, "Worker 1 should be able to release the buffer");
        assertTrue(status_2.error == null, "Worker 2 should be able to release the buffer");
        assertTrue(status_3.error instanceof IllegalStateException, "Worker 3 should fail to release the buffer");
        
        assertTrue(cache.available() == CACHE_SIZE, "Buffer cache should be empty now");
    }
    
    static class Status {
        boolean found = false;
        Exception error;
    }
    
    static class SpanWorker implements Runnable {
        final int id;
        final BufferCache cache;
        final CountDownLatch latch;
        final Status status;
        
        SpanWorker(int id, BufferCache cache, CountDownLatch latch, Status status) {
            this.id = id;
            this.cache = cache;
            this.latch = latch;
            this.status = status;
        }
        
        @Override
        public void run() {
            LogBuffer buffer = null;
            int iteration = 100;
            
            try {
                // The third worker must wait.
                if (id == 3) {
                    iteration = 500;
                    latch.await();
                }
                buffer = cache.next();
                if (buffer != null) {
                    System.out.println("Thread [" + Thread.currentThread() + "] got log buffer."
                    + " Available: " + cache.available());
                
                    status.found = true;
                }
                else {
                    System.out.println("Thread [" + Thread.currentThread() + "] did not get log buffer."
                    + " Available: " + cache.available());
                
                    status.found = false;
                    buffer = new LogBuffer();
                    
                    // Now allow worker #1 and #2 to proceed.
                    Thread.sleep(4000);
                    Thread.yield();
                }
                for (short i = 0; i < iteration; i ++) {
                    buffer.add(i);
                    if (i == 25 && id != 3) {
                        latch.countDown();
                        Thread.sleep(2000);
                        Thread.yield();
                    }
                }
                System.out.println("Thread [" + Thread.currentThread() + "] finished processing");
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                if (id == 3) {
                    try {
                        cache.release(buffer);
                    }
                    catch (IllegalStateException e) {
                        status.error = e;
                    }
                }
                if (status.found) {
                    cache.release(buffer);
                }
            }
        }
        
    }
}
