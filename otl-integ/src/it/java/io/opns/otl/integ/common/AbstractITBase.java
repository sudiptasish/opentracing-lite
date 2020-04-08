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
package io.opns.otl.integ.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sudiptasish Chanda
 */
public abstract class AbstractITBase {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractITBase.class);
    
    private static final String TEST_RESOURCE_FILE = "otl-spring-boot.properties";
    
    protected static void addEnvironmentVariables() {
        InputStream iStream = null;
        try {
            Properties props = new Properties();
            URL url = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(TEST_RESOURCE_FILE);

            String filename = url.getFile();
            iStream = new FileInputStream(filename);
            props.load(iStream);

            String[][] envs = {
                    {"APP_ID", props.getProperty("aim.id")},
                    {"APP_NAME", props.getProperty("app.name")}
            };

            addEnvironmentVariables(envs);
            
            if (logger.isInfoEnabled()) {
                logger.info("Added Environment variables");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (iStream != null) {
                    iStream.close();
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * API to add environment variables just before starting the test case.
     * The input envs array contains the key=value pair.
     *
     * @param envs
     */
    protected static void addEnvironmentVariables(String[][] envs) {
        try {
            Map<String, String> env = System.getenv();
            if (env.getClass().getName().equals("java.util.Collections$UnmodifiableMap")) {
                Field field = env.getClass().getDeclaredField("m");
                field.setAccessible(true);
                Map<String, String> envMap = (Map<String, String>) field.get(env);

                for (int i = 0; i < envs.length; i++) {
                    envMap.put(envs[i][0], envs[i][1]);
                }

                if (logger.isInfoEnabled()) {
                    logger.info("Added {} environment variables.", envs.length);
                }
            }
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void logStart() {
        if (logger.isInfoEnabled()) {
            logger.info("Starting Test Case: {}", new Throwable().getStackTrace()[1].getMethodName());
        }
    }

    protected void logEnd() {
        if (logger.isInfoEnabled()) {
            logger.info("Ended Test Case: {}", new Throwable().getStackTrace()[1].getMethodName());
        }
    }
}
