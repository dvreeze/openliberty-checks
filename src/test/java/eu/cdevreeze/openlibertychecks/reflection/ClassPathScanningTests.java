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

import eu.cdevreeze.openlibertychecks.reflection.internal.ClassPathScanning;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class path scanning tests.
 * <p>
 * This is not a regular unit test.
 *
 * @author Chris de Vreeze
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClassPathScanningTests {

    @Test
    public void testClassPathScanning() throws URISyntaxException {
        Path rootDir = Path.of(
                Objects.requireNonNull(ClassPathScanning.class.getResource("/dummyFile.txt")).toURI()
        ).getParent();

        List<Class<?>> classes = ClassPathScanning.findClasses(rootDir);

        classes.forEach(System.out::println);
        assertTrue(classes.size() >= 75);

        assertTrue(classes.stream().allMatch(c -> c.getPackage().getName().startsWith("eu.cdevreeze.openlibertychecks")));
    }
}
