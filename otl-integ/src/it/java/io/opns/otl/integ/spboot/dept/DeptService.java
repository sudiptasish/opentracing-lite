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
package io.opns.otl.integ.spboot.dept;

import io.opns.otl.integ.common.Storage;
import io.opns.otl.integ.model.Department;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author Sudiptasish Chanda
 */
@Service
public class DeptService {
    
    private final Logger logger = LoggerFactory.getLogger(DeptService.class);
    
    private final Tracer tracer = GlobalTracer.get();
    private final Storage<String, Department> storage = Storage.DEPT_STORAGE;
    
    public void insert(Department dept) {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("DeptService::insert").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            storage.add(dept.getId(), dept);
            if (logger.isInfoEnabled()) {
                logger.info("Added department {} to storage", dept);
            }
        }
        finally {
            span.finish();
        }
    }
    
    public Department select(String id) {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("DeptService::select").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            if (logger.isInfoEnabled()) {
                logger.info("Fetching department for id {} from storage", id);
            }
            return storage.get(id);
        }
        finally {
            span.finish();
        }
    }
    
    public Collection<Department> selectAll() {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("DeptService::selectAll").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            if (logger.isInfoEnabled()) {
                logger.info("Fetching All departments from storage");
            }
            return storage.values();
        }
        finally {
            span.finish();
        }
    }
}
