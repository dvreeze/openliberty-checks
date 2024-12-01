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

package eu.cdevreeze.openlibertychecks.reflection.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Annotation retrieval support (for annotations having retention "runtime").
 *
 * @author Chris de Vreeze
 */
public class AnnotationSupport {

    private AnnotationSupport() {
    }

    public static <T extends Annotation> Optional<T> findDeclaredAnnotation(
            Class<?> clazz,
            Class<T> annotationClass
    ) {
        return Optional.ofNullable(clazz.getDeclaredAnnotation(annotationClass));
    }

    public static <T extends Annotation> Optional<T> findAnnotation(
            Class<?> clazz,
            Class<T> annotationClass
    ) {
        return Optional.ofNullable(clazz.getAnnotation(annotationClass));
    }

    public static <T extends Annotation> Optional<T> findDeclaredAnnotation(
            Constructor<?> constructor,
            Class<T> annotationClass
    ) {
        return Optional.ofNullable(constructor.getDeclaredAnnotation(annotationClass));
    }

    public static <T extends Annotation> Optional<T> findAnnotation(
            Constructor<?> constructor,
            Class<T> annotationClass
    ) {
        return Optional.ofNullable(constructor.getAnnotation(annotationClass));
    }

    public static <T extends Annotation> Optional<T> findDeclaredAnnotation(
            Field field,
            Class<T> annotationClass
    ) {
        return Optional.ofNullable(field.getDeclaredAnnotation(annotationClass));
    }

    public static <T extends Annotation> Optional<T> findAnnotation(
            Field field,
            Class<T> annotationClass
    ) {
        return Optional.ofNullable(field.getAnnotation(annotationClass));
    }

    public static <T extends Annotation> Optional<T> findDeclaredAnnotation(
            Method method,
            Class<T> annotationClass
    ) {
        return Optional.ofNullable(method.getDeclaredAnnotation(annotationClass));
    }

    public static <T extends Annotation> Optional<T> findAnnotation(
            Method method,
            Class<T> annotationClass
    ) {
        return Optional.ofNullable(method.getAnnotation(annotationClass));
    }
}
