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
package io.opns.otl.core;

import io.opentracing.Span;

/**
 * Platform specific visitor.
 * 
 * The visitor design pattern is a way of separating an algorithm from an object
 * structure on which it operates. A visitor is primarily called only after a span
 * is finished. A visitor will traverse through the {@link Span} and collect contextual
 * information only to send them to specific destination.
 *
 * @author Sudiptasish Chanda
 */
public interface OTLSpanVisitor {
    
    /**
     * As part of the API call, all the contextual data will be collected from
     * the span and dispatched to specific destination.
     * The destination could be in memory, standard console or file.
     * 
     * @param span      The span object to be visited.
     * @param param     Callback argument.
     */
    void visit(OTLSpan span, Object param);
}
