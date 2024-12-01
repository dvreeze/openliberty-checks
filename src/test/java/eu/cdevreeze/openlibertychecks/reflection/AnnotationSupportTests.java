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

package eu.cdevreeze.openlibertychecks.reflection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static eu.cdevreeze.openlibertychecks.reflection.internal.AnnotationSupport.findAnnotation;
import static eu.cdevreeze.openlibertychecks.reflection.internal.AnnotationSupport.findDeclaredAnnotation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Annotation retrieval support tests.
 * <p>
 * This is not a regular unit test.
 *
 * @author Chris de Vreeze
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnnotationSupportTests {

    @Test
    public void testDeclaredAnnotationRetrieval() {
        Class<?> clazz = AnnotationSupportTests.class;
        Optional<TestInstance> testInstanceAnnotationOption = findDeclaredAnnotation(clazz, TestInstance.class);

        assertTrue(testInstanceAnnotationOption.isPresent());

        List<Method> declaredMethods =
                Arrays.stream(clazz.getDeclaredMethods())
                        .filter(m -> Modifier.isPublic(m.getModifiers()))
                        .toList();

        assertEquals(
                Stream.of(Boolean.TRUE, Boolean.TRUE).toList(),
                declaredMethods.stream()
                        .map(m -> findDeclaredAnnotation(m, Test.class))
                        .map(Optional::isPresent)
                        .toList()
        );
    }

    @Test
    public void testAnnotationRetrieval() {
        Class<?> clazz = AnnotationSupportTests.class;
        Optional<TestInstance> testInstanceAnnotationOption = findAnnotation(clazz, TestInstance.class);

        assertTrue(testInstanceAnnotationOption.isPresent());

        List<Method> declaredMethods =
                Arrays.stream(clazz.getDeclaredMethods())
                        .filter(m -> Modifier.isPublic(m.getModifiers()))
                        .toList();

        assertEquals(
                Stream.of(Boolean.TRUE, Boolean.TRUE).toList(),
                declaredMethods.stream()
                        .map(m -> findAnnotation(m, Test.class))
                        .map(Optional::isPresent)
                        .toList()
        );
    }
}
