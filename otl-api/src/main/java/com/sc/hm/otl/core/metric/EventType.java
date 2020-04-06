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
package com.sc.hm.otl.core.metric;

/**
 *
 * @author Sudiptasish Chanda
 */
public enum EventType {
    
    SPAN_CREATED ("Indicates the creation of a Span")
    , SPAN_FINISHED ("Indicates that a span has just finished")
    , SCOPE_ATIVATED ("Indicates that a span scope has just been activated")
    , SCOPE_CLOSED ("Indicates that a span scope has just been closed");
    
    final String desc;
    
    EventType(String desc) {
        this.desc = desc;
    }
}
