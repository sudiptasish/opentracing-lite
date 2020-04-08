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
package io.opns.otl.core.log;

import io.opns.otl.util.ObjectCreator;

/**
 * This class hides and serves as a substitute for the underlying logger provider.
 * 
 * <p>
 * This class, will delegate all the calls to the underlying system's logger implementation.
 * It accomplishes the same with the help of {@link LoggerAdapter}. If the
 * associated framework provides an adapter then the same will be loaded by the
 * provider library. If no adapter is found, then the default {@link ConsoleLoggerAdapter}
 * will be used.
 * 
 * <p>
 * Please note that all methods in this class are static.
 * 
 * @author Sudiptasish Chanda
 */
public final class OTLContextLogger {
    
    private static final LoggerAdapter LOGGER_ADAPTER;
    
    static {
        LoggerProvider provider = LoggerProviderFactory.getProvider();
        if (provider != null) {
            LOGGER_ADAPTER = provider.create();
        }
        else {
            String customAdapter = System.getProperty("logger.adapter");
            if (customAdapter != null && (customAdapter = customAdapter.trim()).length() > 0) {
                LOGGER_ADAPTER = ObjectCreator.create(customAdapter);
            }
            else {
                LOGGER_ADAPTER = new ConsoleLoggerAdapter();
            }
        }
    }
    
    /**
     * Log a message by calling the underlying logging framework provider.
     * If no provider found, then the default console adapter will be used to log
     * the message on the console.
     * 
     * @param msg   The message to be logged.
     */
    public static void log(String msg) {
        LOGGER_ADAPTER.log(msg);
    }
}
