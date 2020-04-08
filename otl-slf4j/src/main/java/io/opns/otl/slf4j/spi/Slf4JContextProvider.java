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

import io.opns.otl.core.ctx.ContextProvider;
import io.opns.otl.core.ctx.ContextAdapter;

/**
 * Provider that helps create a Slf4J compliant context adapter.
 *
 * @author Sudiptasish Chanda
 */
public class Slf4JContextProvider implements ContextProvider {
    
    @Override
    public ContextAdapter create() {
        return new Slf4JContextAdapter();
    }
}
