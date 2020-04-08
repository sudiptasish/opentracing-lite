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
package io.opns.otl.jaxrs.filter;

import io.opentracing.propagation.TextMapInject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.MultivaluedMap;

/**
 * This is the container request adapter class that will hold all the header values
 * of the client request.
 *
 * @author Sudiptasish Chanda
 */
public class RequestHeaderAdapter implements TextMapInject {
    
    private final MultivaluedMap<String, Object> headers;

    RequestHeaderAdapter(ClientRequestContext requestCtx) {
        this.headers = requestCtx.getHeaders();
    }

    @Override
    public void put(String key, String value) {
        headers.add(key, value);
    }
}
