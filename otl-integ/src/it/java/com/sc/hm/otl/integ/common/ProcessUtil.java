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
package com.sc.hm.otl.integ.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Utility class to spawn a server process.
 * 
 * @author Sudiptasish Chanda
 */
public final class ProcessUtil {

    private static final Logger logger = LoggerFactory.getLogger(ProcessUtil.class);
    
    private static final String AGENT_JAR = "/Users/schan280/Projects/opentracing-lite/otl-agent/target/otl-agent-1.0-SNAPSHOT.jar";

    // If the embedded mode is off (which is by default), then all the services will
    // be started as separate process. Test framework will use the below command to
    // spawn the individual process.
    // Arguments:
    //    0: Main Class.
    //    1. Program command line argument(s).
    private static final String CMD_TEMPLATE = "java -javaagent:" + AGENT_JAR + " $JAVA_OPTS $DEBUG_OPTS -classpath $CLASSPATH {0} {1}";
    
    // Map to hold the reference(s) of the remote processes started by the framework.
    // If all the services are started as individual process, then the below list
    // will hold the references of individual processes.
    private static final Map<String, Process> PROCESSES = new HashMap<>(8);
    
    private static final String DEBUG = "-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address={0}";

    /**
     * Convenient API to fork a server process/jvm.
     * The configuration object will have the necessary configuration required
     * to start a process.
     *
     * @param configs    Process configuration objects.
     */
    public static void start(ProcessConfig... configs) {
        for (ProcessConfig config : configs) {
            internalStart(config);
        }
    }
    
    /**
     * Convenient API to fork a server process/jvm.
     * The configuration object will have the necessary configuration required
     * to start a process.
     *
     * @param config    Process configuration object.
     */
    public static void internalStart(ProcessConfig config) {
        try {
            // Prepare the process startup command.
            String exec = MessageFormat.format(CMD_TEMPLATE, new Object[]{
                    config.getMainClass(),
                    String.join(" ", config.getArgs())});

            String[] command = {
                    "/bin/sh",
                    "-c",
                    exec
            };

            File baseDir = new File(System.getProperty("user.dir"));
            ProcessBuilder pb =
                    new ProcessBuilder()
                            .directory(baseDir)
                            .command(command)
                            .redirectErrorStream(true)
                            .redirectOutput(ProcessBuilder.Redirect.appendTo(
                                new File(baseDir, config.getLogFile())));

            // Set the environment variables.
            Map<String, String> env = pb.environment();

            // env.put("PATH", "/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin");
            env.put("APP_ID", config.getAppId());
            env.put("APP_NAME", config.getAppName());
            env.put("JAVA_OPTS", config.getJavaOpts());
            env.put("CLASSPATH", System.getProperty("java.class.path"));
            if (config.isDebugEnabled()) {
                env.put("DEBUG_OPTS", MessageFormat.format(DEBUG, String.valueOf(config.getDebugPort())));
            }
            
            // Start the individual processes !
            if (logger.isInfoEnabled()) {
                logger.info("Executing Command: {}. Environment: {}", exec, env);
            }
            // Finally, start the remote process.
            Process process = pb.start();
            PROCESSES.put(config.getAppName(), process);

            Boolean exited = process.waitFor(5000, TimeUnit.MILLISECONDS);
            if (exited) {
                throw new IOException(String.format(
                    "Remote Process [%s] could not be started", config.getAppName()));
            }
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * API to destroy all the remote processes spawned by the test framework.
     */
    public static void destroy() {
        // Now stop the process one by one.
        if (PROCESSES.isEmpty()) {
            logger.warn("No process is up");
            return;
        }
        for (Map.Entry<String, Process> me : PROCESSES.entrySet()) {
            Process process = me.getValue();
            process.destroy();
        }

        if (logger.isWarnEnabled()) {
            logger.warn("Stopped All Processes: {}", PROCESSES.keySet());
        }
        PROCESSES.clear();
    }
}
