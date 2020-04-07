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
package com.sc.hm.otl.agent;

import com.sc.hm.otl.agent.provider.NoOpTracerProvider;
import com.sc.hm.otl.agent.provider.Provider;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import java.lang.instrument.Instrumentation;

/**
 * The OTL Agent.
 * 
 * <p>
 * From Agent javadoc:
 * An agent is deployed as a JAR file. An attribute in the JAR file manifest specifies
 * the agent class which will be loaded to start the agent. For implementations that
 * support a command-line interface, an agent is started by specifying an option on
 * the command-line. Implementations may also support a mechanism to start agents
 * some time after the VM has started. For example, an implementation may provide
 * a mechanism that allows a tool to attach to a running application, and initiate
 * the loading of the tool's agent into the running application. The details as to
 * how the load is initiated, is implementation dependent.
 * 
 * <p>
 * OTL Agent is used to initialize the platform tracer instance and store it in
 * the {@link GlobalTracer} instance. Opentracing GlobalTracer is a singleton instance
 * that instantiate and initialize the platform specific tracer.
 * 
 * It follows the service loader mechanism to find out the available factory APIs,
 * and instantiate the provider specific {@link Tracer}.
 * 
 * The agent must be attached to the jvm during startup with a command-line interface,
 * an agent is started by adding this option to the command-line:
 *     -javaagent:/path/to/agent_jar_file[=options]
 *
 * @author Sudiptasish Chanda
 */
public class OTLAgent {
    
    /**
     * Once the agent is loaded into the memory, it will call the premain method,
     * passing the {@link Instrumentation} instance.
     * 
     * @param args  The command line argument.
     * @param inst  Instrumentation object passed by the jvm during startup.
     */
    public static void premain(String args, Instrumentation inst) {
        initializeTracer();
        initializeMetric();
    }
    
    /**
     * This method will be called if an attempt is made to dynamically load the
     * agent whilst the target VM is up and running.
     * 
     * Using tools' attach API one can load the agent on the fly.
     * 
     * VirtualMachine vm = VirtualMachine.attach(pid);
     * vm.loadAgent(agentFile.getAbsolutePath());
     * vm.detach();
     * 
     * @param args  The command line argument.
     * @param inst  Instrumentation object passed post agent load.
     */
    public static void agentmain(String args, Instrumentation inst) {
        initializeTracer();
        initializeMetric();
    }
    
    /**
     * API to initialize the platform tracer instance.
     */
    private static void initializeTracer() {
        Provider provider = Provider.provider();
        
        if (! (provider instanceof NoOpTracerProvider)) {
            System.out.println("Found provider [" + provider + "]. Creating new tracer instance");
            Tracer tracer = provider.createTracer();
            GlobalTracer.registerIfAbsent(tracer);
            
            System.out.println("Created and registered tracer [" + tracer + "] with GlobalTracer");
        }
        else {
            System.err.println("No Tracer Provider found");
        }
    }
    
    /**
     * Metric sub-system initialization.
     */
    private static void initializeMetric() {
        MetricStartup mStartup = new MetricStartup();
        mStartup.start();
    }
}
