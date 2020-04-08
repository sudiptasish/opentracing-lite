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

import io.opns.otl.core.log.LoggerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sudiptasish Chanda
 */
public class MockLoggerAdapter implements LoggerAdapter {
    
    private final List<String> msgs = new ArrayList<>();

    @Override
    public void log(String msg) {
        msgs.add(msg);
    }

    public List<String> getLogMessages() {
        return msgs;
    }
}
