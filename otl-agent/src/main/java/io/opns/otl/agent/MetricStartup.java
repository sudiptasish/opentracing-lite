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
package io.opns.otl.agent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


/**
 * Class that help registering otl metric sub-system with the VM.
 *
 * @author Sudiptasish Chanda
 */
public class MetricStartup {
    
    private final String METRIC_LIB = "otl-metrics";
    private final String METRIC_BOOTSTRAP = "io.opns.otl.metrics.server.MetricBootstrap";
    
    /**
     * Register the otl metric system with the VM.
     * 
     * The metrics collection will be enabled only if the otl-metrics.version.jar
     * is found in the classpath. If no jar is found in the classpath, then it will
     * be assumed that user has not included the metric jar in runtime, and thus 
     * no metric will ever be collected.
     */
    public void start() {
        try {
            Class clazz = Class.forName(METRIC_BOOTSTRAP);
            Constructor constructor = clazz.getDeclaredConstructor(new Class[] {});
            Object obj = constructor.newInstance(new Object[] {});
            
            // Invoke the method.
            Method method = clazz.getDeclaredMethod("init", new Class[] {});
            method.invoke(obj, new Object[] {});
            
            System.out.println("Successfully initialized metric sub-system");
        }
        catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                System.err.println("Metric sub-system otl-metrics.jar is not"
                    + " found in the classpath.");
            }
            else {
                e.printStackTrace();
            }
        }
    }
}
