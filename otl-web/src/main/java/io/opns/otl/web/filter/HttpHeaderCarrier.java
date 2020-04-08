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
package io.opns.otl.web.filter;

import io.opentracing.propagation.TextMapExtract;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This is the http request header carrier class that will hold all the header values.
 *
 * @author Sudiptasish Chanda
 */
public class HttpHeaderCarrier implements TextMapExtract {
    
    private final Map<String, String> headers = new HashMap<>();
    
    HttpHeaderCarrier(HttpServletRequest request) {
        for (Enumeration<String> enm = request.getHeaderNames(); enm.hasMoreElements(); ) {
            String key = enm.nextElement();
            headers.put(key, request.getHeader(key));
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return headers.entrySet().iterator();
    }
    
}
