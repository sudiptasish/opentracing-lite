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

import io.opentracing.propagation.TextMapExtract;
import io.vertx.core.http.HttpServerRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Vertx span context carrier class that will hold all the header values.
 *
 * @author Sudiptasish Chanda
 */
public class VertxMsgContextCarrier implements TextMapExtract {
    
    private final Map<String, String> headers = new HashMap<>();
    
    VertxMsgContextCarrier(HttpServerRequest request) {
        for (Iterator<Map.Entry<String, String>> itr = request.headers().iterator(); itr.hasNext(); ) {
            Map.Entry<String, String> me = itr.next();
            headers.put(me.getKey(), me.getValue());
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.entrySet().iterator();
    }
    
}
