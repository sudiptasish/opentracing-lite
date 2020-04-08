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
package io.opns.otl.vertx;

import io.opentracing.propagation.TextMapInject;
import io.vertx.core.MultiMap;

/**
 * Vertx delivery context adapter class to be used to inject the span context.
 *
 * @author Sudiptasish Chanda
 */
public class VertxMsgContextAdapter implements TextMapInject {
    
    private final MultiMap header;
    
    VertxMsgContextAdapter(MultiMap header) {
        this.header = header;
    }

    @Override
    public void put(String key, String value) {
        header.add(key, value);
    }
    
}
