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
package io.opns.otl.util;

/**
 *
 * @author Sudiptasish Chanda
 */
public final class OTLConstants {
    
    public static final String TRACE_ID_HEADER = "X-B3-TraceId";
    public static final String SPAN_ID_HEADER = "X-B3-SpanId";
    public static final String BAGGAGE_PREFIX_HEADER = "X-B3-Baggage-";
    public static final String BAGGAGE_ITEMS_HEADER = "X-B3-Baggages";
    public static final String SAMPLED_HEADER = "X-B3-Sampled";
    
    public static final String BAGGAGE_ITEM_SEPARATOR = "=";
    public static final String VERTX_ACTIVE_SPAN = "VAS";
    public static final String VERTX_SCOPE = "VSC";
    
    public static final String DECORATOR = "DECORATOR";
    public static final String SKIP_PATTERN = "SKIP_PATTERN";
    public static final String URL_PATTERN = "URL_PATTERN";
    
    public static final String INCOMPLETE_TAG = "incomplete";
}
