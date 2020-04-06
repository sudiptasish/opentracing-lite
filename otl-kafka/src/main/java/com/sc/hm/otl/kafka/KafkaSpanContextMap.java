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
package com.sc.hm.otl.kafka;

import io.opentracing.propagation.TextMapExtract;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

/**
 * Span context map, where the contextual information will be kept after extracing
 * the same from a kafka message.
 *
 * @author Sudiptasish Chanda
 */
public class KafkaSpanContextMap implements TextMapExtract {
    
    private final Map<String, String> headers = new HashMap<>();
    
    KafkaSpanContextMap(Headers headers) {
        for (Iterator<Header> itr = headers.iterator(); itr.hasNext(); ) {
            Header header = itr.next();
            this.headers.put(header.key(), new String(header.value()));
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.entrySet().iterator();
    }
    
}
