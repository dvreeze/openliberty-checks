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

package eu.cdevreeze.openlibertychecks.xml.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementTree;

import javax.xml.namespace.QName;
import java.util.Optional;
import java.util.OptionalInt;

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementPredicates.hasName;

/**
 * Element named "dataSource" in a server.xml file.
 *
 * @author Chris de Vreeze
 */
public final class DataSource implements ServerXmlContent {

    private final ElementTree.Element element;

    public DataSource(ElementTree.Element element) {
        Preconditions.checkArgument(element.elementName().getLocalPart().equals("dataSource"));
        this.element = element;
    }

    public ElementTree.Element getElement() {
        return element;
    }

    public Optional<String> idOption() {
        return element.attributeOption(new QName("id"));
    }

    public Optional<String> jndiNameOption() {
        return element.attributeOption(new QName("jndiName"));
    }

    public Optional<String> typeOption() {
        return element.attributeOption(new QName("type"));
    }

    public Optional<String> connectionManagerRefOption() {
        return element.attributeOption(new QName("connectionManagerRef"));
    }

    public ImmutableList<JdbcDriver> jdbcDrivers() {
        return element.childElementStream(hasName("jdbcDriver"))
                .map(JdbcDriver::new)
                .collect(ImmutableList.toImmutableList());
    }

    public ImmutableList<Properties> propertiesElements() {
        return element.childElementStream(hasName("properties"))
                .map(Properties::new)
                .collect(ImmutableList.toImmutableList());
    }

    public static final class Properties implements ServerXmlContent {

        private final ElementTree.Element element;

        public Properties(ElementTree.Element element) {
            Preconditions.checkArgument(element.elementName().getLocalPart().equals("properties"));
            Preconditions.checkArgument(element.parentElementOption().isPresent());
            Preconditions.checkArgument(
                    element.parentElementOption().orElseThrow().elementName().getLocalPart().equals("dataSource")
            );
            this.element = element;
        }

        public ElementTree.Element getElement() {
            return element;
        }

        public Optional<String> databaseNameOption() {
            return element.attributeOption(new QName("databaseName"));
        }

        public Optional<String> passwordOption() {
            return element.attributeOption(new QName("password"));
        }

        public Optional<String> serverNameOption() {
            return element.attributeOption(new QName("serverName"));
        }

        public Optional<String> userOption() {
            return element.attributeOption(new QName("user"));
        }

        // In case configuration variables have not yet been resolved

        public Optional<String> portNumberAsStringOption() {
            return element.attributeOption(new QName("portNumber"));
        }

        public OptionalInt portNumberOption() {
            return portNumberAsStringOption()
                    .stream()
                    .mapToInt(Integer::parseInt)
                    .findFirst();
        }
    }
}
