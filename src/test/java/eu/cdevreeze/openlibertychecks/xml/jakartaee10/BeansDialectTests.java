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

package eu.cdevreeze.openlibertychecks.xml.jakartaee10;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.cdi.*;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.Document;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Beans (CDI) dialect tests.
 * <p>
 * This is not a regular unit test.
 *
 * @author Chris de Vreeze
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BeansDialectTests {

    private Document parseDocument() {
        InputStream inputStream = BeansDialectTests.class.getResourceAsStream("/dialects/sample-beans.xml");
        return Document.from(
                DocumentParsers.builder().removingInterElementWhitespace().build()
                        .parse(new InputSource(inputStream))
        );
    }

    @Test
    public void testBeansXmlParsing() {
        Document doc = parseDocument();

        Beans beans = new Beans(doc.documentElement());

        String ns = beans.getElement().elementName().getNamespaceURI();
        assertEquals(Names.JAKARTAEE_NS, ns);

        Optional<Scan> scanOption = beans.scanOption();

        assertTrue(scanOption.isPresent());

        Scan scan = scanOption.orElseThrow();

        ImmutableList<Exclude> excludes = scan.excludes();

        assertEquals(4, excludes.size());

        assertEquals(
                List.of(
                        List.of(
                                List.of(),
                                List.of(),
                                List.of()
                        ),
                        List.of(
                                List.of(),
                                List.of("javax.faces.context.FacesContext"),
                                List.of()
                        ),
                        List.of(
                                List.of(),
                                List.of(),
                                List.of("verbosity")
                        ),
                        List.of(
                                List.of("javax.enterprise.inject.Model"),
                                List.of(),
                                List.of("exclude-ejbs")
                        )
                ),
                excludes.stream()
                        .map(e -> List.of(
                                e.ifClassAvailableElements().stream().map(IfClassAvailable::name).toList(),
                                e.ifClassNotAvailableElements().stream().map(IfClassNotAvailable::name).toList(),
                                e.ifSystemPropertyElements().stream().map(IfSystemProperty::name).toList()
                        ))
                        .toList()
        );
    }
}
