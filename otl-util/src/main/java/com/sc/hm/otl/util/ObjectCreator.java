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
package com.sc.hm.otl.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Sudiptasish Chanda
 */
public class ObjectCreator {
    
    /**
     * Create and return the instance of the class designated by this className.
     * It uses the no-argument constructor (default one) while instantiating
     * the object (provided the no-argument constructor is defined).
     * 
     * @param   <T>
     * @param   className           Class name
     * 
     * @return  T                   New Instance
     */
    @SuppressWarnings("unchecked")
	public static <T> T create(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className.trim());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        try {
            Constructor<T> constructor = (Constructor<T>)clazz.getDeclaredConstructor(new Class[] {});
            T obj = constructor.newInstance(new Object[] {});
            return obj;
        }
        catch (NoSuchMethodException
            | InstantiationException
            | IllegalAccessException
            | InvocationTargetException e) {
            
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Create and return the instance of the class designated by this className.
     * It uses the specific parameterized constructor while instantiating the class
     * instance.
     * 
     * @param   <T>
     * @param   className           Class name
     * 
     * @return  T                   New Instance
     */
    @SuppressWarnings("unchecked")
	public static <T> T create(String className
                               , Class[] paramTypes
                               , Object[] params) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className.trim());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        try {
            Constructor<T> constructor = (Constructor<T>)clazz.getDeclaredConstructor(paramTypes);
            T obj = constructor.newInstance(params);
            return obj;
        }
        catch (NoSuchMethodException
            | InstantiationException
            | IllegalAccessException
            | InvocationTargetException e) {
            
            throw new RuntimeException(e);
        }
    }
}
