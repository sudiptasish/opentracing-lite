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
package com.sc.hm.otl.metrics.mbean;

/**
 *
 * @author Sudiptasish Chanda
 */
public class LoggingMXBeanImpl implements LoggingMXBean {
    
    private String contextAdapter = "";
    private String loggerFactoryProvider = "";
    private String loggerAdapter = "";

    @Override
    public String getContextAdapter() {
        return contextAdapter;
    }

    @Override
    public String getLoggerFactoryProvider() {
        return loggerFactoryProvider;
    }

    @Override
    public String getLoggerAdapter() {
        return loggerAdapter;
    }

    public void setContextAdapter(String contextAdapter) {
        this.contextAdapter = contextAdapter;
    }

    public void setLoggerFactoryProvider(String loggerFactoryProvider) {
        this.loggerFactoryProvider = loggerFactoryProvider;
    }

    public void setLoggerAdapter(String loggerAdapter) {
        this.loggerAdapter = loggerAdapter;
    }
    
}
