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

package eu.cdevreeze.openlibertychecks.xml.ibm.server;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.Document;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementPredicates.hasName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OpenLiberty server dialect tests.
 * <p>
 * This is not a regular unit test.
 *
 * @author Chris de Vreeze
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerDialectTests {

    private Document parseDocument() {
        InputStream inputStream = ServerDialectTests.class.getResourceAsStream("/dialects/sample-server.xml");
        return Document.from(
                DocumentParsers.builder().removingInterElementWhitespace().build()
                        .parse(new InputSource(inputStream))
        );
    }

    @Test
    public void testServerXmlParsing() {
        Document doc = parseDocument();

        Server server = new Server(doc.documentElement());

        String ns = server.getElement().elementName().getNamespaceURI();
        assertEquals("", ns);

        assertEquals(
                List.of("servlet-3.0", "localConnector-1.0"),
                server.featureManagers().stream()
                        .flatMap(fm -> fm.features().stream())
                        .toList()
        );

        assertEquals(
                List.of(
                        List.of("snoop", "/mywebapps/snoop", "snoop", "war")
                ),
                server.getElement().childElementStream(hasName("application"))
                        .map(e -> List.of(
                                e.attribute(new QName("name")),
                                e.attribute(new QName("location")),
                                e.attribute(new QName("id")),
                                e.attribute(new QName("type"))
                        ))
                        .toList()
        );

        assertEquals(
                List.of("30s"),
                server.transactions()
                        .stream()
                        .flatMap(e -> e.totalTranLifetimeTimeoutOption().stream())
                        .toList()
        );

        ImmutableList<DataSource> dataSources = server.dataSources();

        assertEquals(1, dataSources.size());
        DataSource dataSource = dataSources.get(0);

        assertEquals(Optional.of("blogDS"), dataSource.idOption());
        assertEquals(Optional.of("jdbc/blogDS"), dataSource.jndiNameOption());
        assertEquals(Optional.of("derbyPool"), dataSource.connectionManagerRefOption());

        // This connectionManagerRef must resolve to a connectionManager

        assertTrue(
                server.connectionManagers().stream()
                        .anyMatch(cm -> cm.idOption().equals(Optional.of("derbyPool")))
        );

        assertEquals(
                List.of(List.of("derbyPool", "10")),
                server.connectionManagers().stream()
                        .map(cm -> List.of(cm.idOption().orElse(""), String.valueOf(cm.maxPoolSize())))
                        .toList()
        );

        assertEquals(
                List.of(Optional.of("C:/liberty/basics/derby/data/blogDB")),
                dataSource.propertiesElements().stream()
                        .map(DataSource.Properties::databaseNameOption)
                        .toList()
        );

        assertEquals(
                List.of(Optional.of("derbyLib")),
                dataSource.jdbcDrivers().stream()
                        .map(JdbcDriver::libraryRefOption)
                        .toList()
        );

        // This libraryRef must resolve to a connectionManager

        assertTrue(
                server.libraries().stream()
                        .anyMatch(lib -> lib.idOption().equals(Optional.of("derbyLib")))
        );

        assertEquals(
                Optional.of(Optional.of("derbyLib")),
                server.libraries().stream()
                        .map(Library::idOption)
                        .findFirst()
        );

        assertEquals(
                List.of(
                        List.of(Optional.of("C:/liberty/basics/derby"), Optional.of("derby.jar"))
                ),
                server.libraries().stream()
                        .flatMap(lib -> lib.filesets().stream())
                        .map(fs -> List.of(fs.dirOption(), fs.includesOption()))
                        .toList()
        );
    }
}
