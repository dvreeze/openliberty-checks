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

package eu.cdevreeze.openlibertychecks.reflection.jakartaee10.cdi;

import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static eu.cdevreeze.openlibertychecks.reflection.internal.AnnotationSupport.findDeclaredAnnotation;

/**
 * CDI annotation support (for annotations having retention "runtime").
 *
 * @author Chris de Vreeze
 */
public class CdiAnnotations {

    private CdiAnnotations() {
    }

    public static boolean hasInjectAnnotation(Constructor<?> constructor) {
        return findInjectAnnotation(constructor).isPresent();
    }

    public static Optional<Inject> findInjectAnnotation(Constructor<?> constructor) {
        return findDeclaredAnnotation(constructor, Inject.class);
    }

    public static boolean hasInjectAnnotation(Field field) {
        return findInjectAnnotation(field).isPresent();
    }

    public static Optional<Inject> findInjectAnnotation(Field field) {
        return findDeclaredAnnotation(field, Inject.class);
    }

    public static boolean hasInjectAnnotation(Method method) {
        return findInjectAnnotation(method).isPresent();
    }

    public static Optional<Inject> findInjectAnnotation(Method method) {
        return findDeclaredAnnotation(method, Inject.class);
    }
}
