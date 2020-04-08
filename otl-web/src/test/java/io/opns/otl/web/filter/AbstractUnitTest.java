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
package io.opns.otl.web.filter;

import io.opns.otl.core.OTLSpanVisitor;
import io.opns.otl.core.impl.OTLTracer;
import io.opns.otl.core.impl.OTLProvider;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeAll;

/**
 *
 * @author Sudiptasish Chanda
 */
public abstract class AbstractUnitTest {
    
    protected static Tracer tracer;
    
    @BeforeAll
    public static void init() {
        System.setProperty("span.visitor", "io.opns.otl.web.filter.MockSpanVisitor");
        initTracer();
    }
    
    private static void initTracer() {
        try {
            GlobalTracer.registerIfAbsent(new OTLProvider().createTracer());
        }
        catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        }
        tracer = GlobalTracer.get();
    }
    
    protected OTLSpanVisitor extract() {
        try {
            Field field = GlobalTracer.class.getDeclaredField("tracer");
            field.setAccessible(true);
            OTLTracer otlTracer = (OTLTracer)field.get(tracer);
            return otlTracer.visitor();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
