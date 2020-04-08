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
package io.opns.otl.integ.spboot.emp;

import io.opns.otl.integ.common.Storage;
import io.opns.otl.integ.model.Employee;
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
public class EmployeeService {
    
    private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    
    private final Tracer tracer = GlobalTracer.get();
    private final Storage<String, Employee> storage = Storage.getEmpDB();
    
    public void insert(Employee emp) {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("EmployeeService::insert").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            storage.add(emp.getId(), emp);
            if (logger.isInfoEnabled()) {
                logger.info("Added employee {} to storage {}. Current count: {}"
                    , emp, this, storage.size());
            }
        }
        finally {
            span.finish();
        }
    }
    
    public Employee select(String id) {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("EmployeeService::select").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            if (logger.isInfoEnabled()) {
                logger.info("Fetching employee for id {} from storage {}. Current count: {}"
                    , id, this, storage.size());
            }
            return storage.get(id);
        }
        finally {
            span.finish();
        }
    }
    
    public Collection<Employee> selectAll() {
        // Create a new span to represent work done at service layer.
        Span span = tracer.buildSpan("EmployeeService::selectAll").start();
        
        try (Scope scope = tracer.activateSpan(span)) {
            if (logger.isInfoEnabled()) {
                logger.info("Fetching All employees from storage");
            }
            return storage.values();
        }
        finally {
            span.finish();
        }
    }
}
