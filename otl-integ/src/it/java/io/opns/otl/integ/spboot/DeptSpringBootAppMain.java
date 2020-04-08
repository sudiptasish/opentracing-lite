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
package io.opns.otl.integ.spboot;
    
import io.opns.otl.spboot.OTLSpringBootConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Sudiptasish Chanda
 */
@SpringBootApplication
@Import({OTLSpringBootConfig.class})
@ComponentScan(basePackages = "io.opns.otl.integ")
public class DeptSpringBootAppMain {
    
    public static void main(String[] args) {
        SpringApplication.run(DeptSpringBootAppMain.class, args);
    }
}