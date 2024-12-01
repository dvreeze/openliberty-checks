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

package eu.cdevreeze.openlibertychecks.console;

import com.google.common.base.Preconditions;
import eu.cdevreeze.openlibertychecks.reflection.internal.ClassPathScanning;
import jakarta.annotation.Resource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static eu.cdevreeze.openlibertychecks.reflection.jakartaee10.CommonJakartaAnnotations.findResourceAnnotation;
import static eu.cdevreeze.openlibertychecks.reflection.jakartaee10.CommonJakartaAnnotations.findResourcesAnnotation;

/**
 * Program finding the resources in an extracted WAR file directory.
 * These resources may occur as Resource-annotated code or resources in XML configuration files.
 * The resources are compared with the contents of OpenLiberty configuration files.
 * <p>
 * To run this program, first complete the class path for running. For example, obtain the class path
 * from the analysed project by running command "mvn dependency:build-classpath", for example.
 * <p>
 * This program takes at least one directory path. The first one contains the open WAR directory.
 * The other ones contain OpenLiberty configuration files.
 *
 * @author Chris de Vreeze
 */
public class FindResourcesInWar {

    public record ResourceInfo(
            Class<?> containingClass,
            Resource resourceAnnotation,
            String jndiName,
            Optional<String> bindingOption
    ) {
    }

    public static void main(String[] args) {
        Objects.checkIndex(0, args.length);

        Path warPath = Path.of(args[0]);
        List<Path> otherPaths = IntStream.range(1, args.length).mapToObj(i -> Path.of(args[i])).toList();

        List<ResourceInfo> foundResources = findResources(warPath, otherPaths);

        System.out.println(foundResources);
    }

    public static List<ResourceInfo> findResources(Path warDir, List<Path> otherDirs) {
        Preconditions.checkArgument(Files.isDirectory(warDir));
        Preconditions.checkArgument(otherDirs.stream().allMatch(Files::isDirectory));

        // TODO Use other directories

        Map<Class<?>, List<Resource>> resources = findResourcesInClassesDir(warDir);

        return resources.entrySet().stream()
                .flatMap(kv ->
                        kv.getValue().stream()
                                .map(res ->
                                        new ResourceInfo(kv.getKey(), res, res.name(), Optional.empty())
                                )
                )
                .toList();
    }

    public static Map<Class<?>, List<Resource>> findResourcesInClassesDir(Path warDir) {
        Path classesDir = warDir.resolve("WEB-INF").resolve("classes");
        Preconditions.checkArgument(Files.isDirectory(classesDir));

        List<Class<?>> webAppClasses = ClassPathScanning.findClasses(classesDir);

        return webAppClasses.stream()
                .map(c -> Map.entry(c, findAllResourcesInClass(c)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static List<Resource> findAllResourcesInClass(Class<?> clazz) {
        List<Resource> resourcesInClass =
                findResourceAnnotation(clazz).stream().toList();
        List<Resource> resourcesInFields =
                getFields(clazz).stream().flatMap(f -> findResourceAnnotation(f).stream()).toList();
        List<Resource> resourcesInMethods =
                getMethods(clazz).stream().flatMap(m -> findResourceAnnotation(m).stream()).toList();
        List<Resource> resourcesBundledInClass =
                findResourcesAnnotation(clazz).stream()
                        .flatMap(r -> Arrays.stream(r.value()))
                        .toList();
        return Stream.of(resourcesInClass, resourcesInFields, resourcesInMethods, resourcesBundledInClass)
                .flatMap(List::stream)
                .toList();
    }

    private static List<Field> getFields(Class<?> clazz) {
        return Stream.of(Arrays.stream(clazz.getDeclaredFields()), Arrays.stream(clazz.getFields()))
                .flatMap(v -> v)
                .distinct()
                .toList();
    }

    private static List<Method> getMethods(Class<?> clazz) {
        return Stream.of(Arrays.stream(clazz.getDeclaredMethods()), Arrays.stream(clazz.getMethods()))
                .flatMap(v -> v)
                .distinct()
                .toList();
    }
}
