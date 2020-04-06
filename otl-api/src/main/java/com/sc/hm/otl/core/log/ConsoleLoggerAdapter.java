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
package com.sc.hm.otl.core.log;

import java.io.PrintStream;

/**
 * A console logger adapter, which dumps any span data onto the standard output.
 *
 * @author Sudiptasish Chanda
 */
public class ConsoleLoggerAdapter implements LoggerAdapter {
    
    private final PrintStream console = System.out;

    @Override
    public void log(String msg) {
        console.println(msg);
    }
    
}
