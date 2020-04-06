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

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to spawn a server process.
 */
public final class ProcessConfig {

    private String appId = "";
    private String appName;
    private String mainClass = "";
    private String javaOpts = "";
    private int debugPort = -1;
    private boolean debugEnabled = false;
    private String[] args = new String[0];
    
    private Map<String, String> systemProps = new HashMap<>();
    private String logFile;
    
    public ProcessConfig() {}

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getJavaOpts() {
        return javaOpts;
    }

    public void setJavaOpts(String javaOpts) {
        this.javaOpts = javaOpts;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String... args) {
        this.args = args;
    }

    public Map<String, String> getSystemProps() {
        return systemProps;
    }

    public void addSystemProps(String key, String value) {
        this.systemProps.put(key, value);
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }
}
