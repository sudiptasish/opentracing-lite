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

/**
 * A Span Logger.
 * 
 * Often user wants to dump to useful information about a span or an event while
 * a specific span is active. The {@link LoggerAdapter} helps dumping the information
 * to a specific destination as specified.
 * There can be different implementation of span logger, which sends events/messages
 * to it's own destination.
 *
 * @author Sudiptasish Chanda
 */
public interface LoggerAdapter {
    
    /**
     * Log the message to appropriate destination.
     * The logging will always be done at INFO level.
     * 
     * @param msg   The message to be logged.
     */
    void log(String msg);
}
