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
package com.sc.hm.otl.jaxrs.filter;

import io.opentracing.propagation.TextMapExtract;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * This is the container request adapter class that will hold all the header values.
 *
 * @author Sudiptasish Chanda
 */
public class RequestHeaderCarrier implements TextMapExtract {
    
    private final Map<String, String> headers = new HashMap<>();

    RequestHeaderCarrier(ContainerRequestContext requestCtx) {
        List<String> values = null;
        
        for (Map.Entry<String, List<String>> me : requestCtx.getHeaders().entrySet()) {
            values = me.getValue();
            if (values.size() > 1) {
                headers.put(me.getKey(), String.join(",", values.toArray(new String[values.size()])));
            }
            else {
                headers.put(me.getKey(), values.get(0));
            }
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.entrySet().iterator();
    }
}
