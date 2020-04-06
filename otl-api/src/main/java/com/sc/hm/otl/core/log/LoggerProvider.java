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
package com.sc.hm.otl.core.log;

/**
 * Logger provider interface.
 * 
 * The span data, by default is written to the standard console output. However,
 * while in production, it should be integrated with the underlying logging 
 * framework (if any) to get benefit from it. If there is a provider found, then
 * that provider will be used to bridge the gap between the trace layer and application
 * layer in order to propagate the log to specific destination.
 * otl framework comes with a Slf4J provider. Today the major logging framework,
 * e.g., log4j2, logback, etc follows the SLf4J spec, thus making it easier to
 * seemlessly integrate with the underlying logging framework via an {@link LoggerAdapter}.
 *
 * @author Sudiptasish Chanda
 */
public interface LoggerProvider {
    
    /**
     * API to create a new instance of {@link LoggerAdapter}.
     * @return LoggerAdapter
     */
    LoggerAdapter create();
}
