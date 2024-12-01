/*
 * Copyright 2024-2024 Chris de Vreeze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cdevreeze.openlibertychecks.reflection.jakartaee10;

import jakarta.annotation.Resource;
import jakarta.annotation.Resources;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static eu.cdevreeze.openlibertychecks.reflection.internal.AnnotationSupport.findAnnotation;

/**
 * Jakarta common annotation support (for annotations having retention "runtime").
 *
 * @author Chris de Vreeze
 */
public class CommonJakartaAnnotations {

    private CommonJakartaAnnotations() {
    }

    public static boolean isResource(Class<?> clazz) {
        return findResourceAnnotation(clazz).isPresent();
    }

    public static Optional<Resource> findResourceAnnotation(Class<?> clazz) {
        return findAnnotation(clazz, Resource.class);
    }

    public static boolean isResource(Field field) {
        return findResourceAnnotation(field).isPresent();
    }

    public static Optional<Resource> findResourceAnnotation(Field field) {
        return findAnnotation(field, Resource.class);
    }

    public static boolean isResource(Method method) {
        return findResourceAnnotation(method).isPresent();
    }

    public static Optional<Resource> findResourceAnnotation(Method method) {
        return findAnnotation(method, Resource.class);
    }

    public static boolean hasResourcesAnnotation(Class<?> clazz) {
        return findResourcesAnnotation(clazz).isPresent();
    }

    public static Optional<Resources> findResourcesAnnotation(Class<?> clazz) {
        return findAnnotation(clazz, Resources.class);
    }
}
