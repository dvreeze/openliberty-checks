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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.cdevreeze.openlibertychecks.reflection.internal.ClassPathScanning;
import eu.cdevreeze.openlibertychecks.xml.ibm.server.JndiEntry;
import eu.cdevreeze.openlibertychecks.xml.ibm.server.Server;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.Names;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.ResourceRef;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.servlet.WebApp;
import eu.cdevreeze.yaidom4j.core.NamespaceScope;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.Document;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementTree;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Element;
import eu.cdevreeze.yaidom4j.dom.immutabledom.Node;
import eu.cdevreeze.yaidom4j.dom.immutabledom.NodeBuilder;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParser;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentPrinter;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentPrinters;
import jakarta.annotation.Resource;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.*;
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
 * To run this program, first complete the class path for running. For example, obtain (most of) the class path
 * from the analysed project by running command "mvn dependency:build-classpath", for example.
 * <p>
 * This program takes at least one directory path. The first one contains the open WAR directory.
 * The other ones contain OpenLiberty configuration files.
 *
 * @author Chris de Vreeze
 */
public class FindResourcesInWar {

    public record ResourceAnnotationInfo(
            AnnotatedElement annotatedElement,
            Resource resourceAnnotation
    ) {

        public Element toXml() {
            var nb = new NodeBuilder.ConciseApi(NamespaceScope.empty());

            return nb.element(
                    "resourceAnnotationOccurrence",
                    ImmutableMap.of(),
                    ImmutableList.of(convertAnnotatedElementToXml(), convertResourceAnnotationToXml())
            );
        }

        private Element convertAnnotatedElementToXml() {
            var nb = new NodeBuilder.ConciseApi(NamespaceScope.empty());

            if (annotatedElement() instanceof Class<?> c) {
                return nb.textElement("class", c.toString());
            } else if (annotatedElement() instanceof Field f) {
                return nb.textElement(
                        "field",
                        ImmutableMap.of("class", f.getDeclaringClass().toString()),
                        f.toString()
                );
            } else if (annotatedElement() instanceof Method m) {
                return nb.textElement(
                        "method",
                        ImmutableMap.of("class", m.getDeclaringClass().toString()),
                        m.toString()
                );
            } else if (annotatedElement() instanceof Constructor<?> c) {
                return nb.textElement(
                        "constructor",
                        ImmutableMap.of("class", c.getDeclaringClass().toString()),
                        c.toString()
                );
            } else if (annotatedElement() instanceof Module m) {
                return nb.textElement("module", m.toString());
            } else if (annotatedElement() instanceof Package p) {
                return nb.textElement("package", p.toString());
            } else if (annotatedElement() instanceof Parameter p) {
                return nb.textElement(
                        "parameter",
                        ImmutableMap.of("executable", p.getDeclaringExecutable().toString()),
                        p.toString()
                );
            } else if (annotatedElement() instanceof RecordComponent c) {
                return nb.textElement("recordComponent", c.toString());
            } else {
                return nb.textElement("annotatedElement", annotatedElement().toString());
            }
        }

        private Element convertResourceAnnotationToXml() {
            var nb = new NodeBuilder.ConciseApi(NamespaceScope.empty());

            return nb.element(
                    "annotation",
                    ImmutableMap.of("annotationType", Resource.class.toString()),
                    ImmutableList.of(
                            nb.textElement("name", resourceAnnotation().name()),
                            nb.textElement("description", resourceAnnotation().description()),
                            nb.textElement("shareable", String.valueOf(resourceAnnotation().shareable())),
                            nb.textElement("lookup", resourceAnnotation().lookup()),
                            nb.textElement("type", resourceAnnotation().type().toString()),
                            nb.textElement("mappedName", resourceAnnotation().mappedName()),
                            nb.textElement("authenticationType", resourceAnnotation().authenticationType().name())
                    )
            );
        }
    }

    public static void main(String[] args) {
        Objects.checkIndex(0, args.length);

        Path warPath = Path.of(args[0]);
        List<Path> otherPaths = IntStream.range(1, args.length).mapToObj(i -> Path.of(args[i])).toList();

        Element foundResources = findResourcesAsXml(warPath, otherPaths);

        DocumentPrinter docPrinter = DocumentPrinters.instance();
        System.out.println(docPrinter.print(foundResources));
    }

    public static Element findResourcesAsXml(Path warDir, List<Path> otherDirs) {
        Preconditions.checkArgument(Files.isDirectory(warDir));
        Preconditions.checkArgument(otherDirs.stream().allMatch(Files::isDirectory));

        Map<AnnotatedElement, List<Resource>> resources = findResourcesInClassesDir(warDir);

        List<ResourceAnnotationInfo> resourceAnnotationInfoList = resources.entrySet().stream()
                .flatMap(kv -> kv.getValue().stream().map(res -> new ResourceAnnotationInfo(kv.getKey(), res)))
                .toList();

        var nb = new NodeBuilder.ConciseApi(NamespaceScope.empty());

        Element resourceAnnotationsElement = nb.element(
                "resourceAnnotationOccurrences",
                ImmutableMap.of(),
                resourceAnnotationInfoList
                        .stream()
                        .map(ResourceAnnotationInfo::toXml)
                        .collect(ImmutableList.toImmutableList())
        );

        ImmutableList<Node> resourceRefs = findResourceRefsInWebXmlFiles(otherDirs)
                .stream()
                .map(e ->
                        nb.element(
                                        "resourceRef",
                                        ImmutableMap.of("doc", e.getElement().docUriOption().map(java.net.URI::toString).orElse(""))
                                )
                                .plusChild(e.getElement().underlyingNode())
                )
                .collect(ImmutableList.toImmutableList());

        ImmutableList<Node> jndiEntries = findJndiEntriesInServerXmlFiles(otherDirs)
                .stream()
                .map(e ->
                        nb.element(
                                        "jndiEntry",
                                        ImmutableMap.of("doc", e.getElement().docUriOption().map(java.net.URI::toString).orElse(""))
                                )
                                .plusChild(e.getElement().underlyingNode())
                )
                .collect(ImmutableList.toImmutableList());

        return nb.element("resourceSummary")
                .plusChild(resourceAnnotationsElement)
                .plusChild(
                        nb.element("resourceRefs").withChildren(resourceRefs)
                )
                .plusChild(
                        nb.element("jndiEntries").withChildren(jndiEntries)
                );
    }

    public static Map<AnnotatedElement, List<Resource>> findResourcesInClassesDir(Path warDir) {
        Path classesDir = warDir.resolve("WEB-INF").resolve("classes");
        Preconditions.checkArgument(Files.isDirectory(classesDir));

        List<Class<?>> webAppClasses = ClassPathScanning.findClasses(classesDir);

        return webAppClasses.stream()
                .flatMap(c -> findAllResourcesInClass(c).entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static List<ResourceRef> findResourceRefsInWebXmlFiles(List<Path> dirs) {
        List<ElementTree.Element> webXmlRoots = dirs.stream()
                .flatMap(dir -> findWebXmlRootElements(dir).stream())
                .toList();

        return webXmlRoots.stream()
                .map(WebApp::new)
                .flatMap(e -> e.resourceRefs().stream())
                .toList();
    }

    public static List<JndiEntry> findJndiEntriesInServerXmlFiles(List<Path> dirs) {
        List<ElementTree.Element> serverXmlRoots = dirs.stream()
                .flatMap(dir -> findServerXmlRootElements(dir).stream())
                .toList();

        return serverXmlRoots.stream()
                .map(Server::new)
                .flatMap(e -> e.jndiEntries().stream())
                .toList();
    }

    private static Map<AnnotatedElement, List<Resource>> findAllResourcesInClass(Class<?> clazz) {
        List<Resource> resourcesInClass =
                findResourceAnnotation(clazz).stream().toList();
        Map<Field, List<Resource>> resourcesInFields =
                getFields(clazz).stream()
                        .flatMap(f -> findResourceAnnotation(f).stream().map(ann -> Map.entry(f, List.of(ann))))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Method, List<Resource>> resourcesInMethods =
                getMethods(clazz).stream()
                        .flatMap(m -> findResourceAnnotation(m).stream().map(ann -> Map.entry(m, List.of(ann))))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<Resource> resourcesBundledInClass =
                findResourcesAnnotation(clazz).stream()
                        .flatMap(r -> Arrays.stream(r.value()))
                        .toList();

        List<Resource> allResourcesInClass = new ArrayList<>();
        allResourcesInClass.addAll(resourcesInClass);
        allResourcesInClass.addAll(resourcesBundledInClass);

        Map<AnnotatedElement, List<Resource>> result = new HashMap<>();
        result.put(clazz, List.copyOf(allResourcesInClass));
        result.putAll(resourcesInFields);
        result.putAll(resourcesInMethods);

        return Map.copyOf(result);
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

    private static List<ElementTree.Element> findWebXmlRootElements(Path dir) {
        DocumentParser docParser = DocumentParsers.builder().removingInterElementWhitespace().build();

        try (Stream<Path> fileStream = Files.walk(dir)) {
            return fileStream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().endsWith(".xml"))
                    .map(p -> Document.from(docParser.parse(p.toUri())).documentElement())
                    .filter(e -> e.name().equals(Names.JAKARTAEE_WEBAPP_NAME))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<ElementTree.Element> findServerXmlRootElements(Path dir) {
        DocumentParser docParser = DocumentParsers.builder().removingInterElementWhitespace().build();

        try (Stream<Path> fileStream = Files.walk(dir)) {
            return fileStream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().endsWith(".xml"))
                    .map(p -> Document.from(docParser.parse(p.toUri())).documentElement())
                    .filter(e -> e.name().equals(new QName("server")))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
