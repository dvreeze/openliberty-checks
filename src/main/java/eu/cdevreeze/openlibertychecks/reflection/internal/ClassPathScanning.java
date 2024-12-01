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

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class path scanning support.
 *
 * @author Chris de Vreeze
 */
public class ClassPathScanning {

    private ClassPathScanning() {
    }

    // Naive implementation

    public static List<Class<?>> findClasses(Path rootDir) {
        int maxDepth = 100;
        try (Stream<Path> pathStream = Files.walk(rootDir, maxDepth)) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".class"))
                    .flatMap(p -> findClass(p, rootDir).stream())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Optional<Class<?>> findClass(Path classFile, Path rootDir) {
        try {
            Preconditions.checkArgument(Files.isRegularFile(classFile));
            Preconditions.checkArgument(classFile.getFileName().toString().endsWith(".class"));

            Path relativePath = rootDir.relativize(classFile.getParent());
            String packageName =
                    String.join(".", IntStream.range(0, relativePath.getNameCount())
                            .mapToObj(relativePath::getName)
                            .map(Path::toString)
                            .toList());
            String simpleClassName = removeClassExtension(classFile.getFileName().toString());

            String fqcn =
                    (packageName.isEmpty()) ? simpleClassName : String.format("%s.%s", packageName, simpleClassName);

            return Optional.of(Class.forName(fqcn));
        } catch (ClassNotFoundException | RuntimeException e) {
            return Optional.empty();
        }
    }

    private static String removeClassExtension(String fileName) {
        if (fileName.endsWith(".class")) {
            return fileName.substring(0, fileName.length() - ".class".length());
        } else {
            return fileName;
        }
    }
}
