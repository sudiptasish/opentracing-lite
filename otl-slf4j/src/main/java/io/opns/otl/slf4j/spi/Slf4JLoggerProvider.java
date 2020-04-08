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
package io.opns.otl.slf4j.spi;

import io.opns.otl.core.log.LoggerAdapter;
import io.opns.otl.core.log.LoggerProvider;

/**
 * An Slf4J compliant logger provider.
 *
 * @author Sudiptasish Chanda
 */
public class Slf4JLoggerProvider implements LoggerProvider {

    @Override
    public LoggerAdapter create() {
        return new Slf4JLoggerAdapter();
    }
    
}
