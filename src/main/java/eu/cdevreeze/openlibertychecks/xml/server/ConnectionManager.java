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
import eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementTree;

import javax.xml.namespace.QName;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Element named "connectionManager" in a server.xml file.
 *
 * @author Chris de Vreeze
 */
public final class ConnectionManager implements ServerXmlContent {

    private final ElementTree.Element element;

    public ConnectionManager(ElementTree.Element element) {
        Preconditions.checkArgument(element.elementName().getLocalPart().equals("connectionManager"));
        this.element = element;
    }

    public ElementTree.Element getElement() {
        return element;
    }

    public Optional<String> idOption() {
        return element.attributeOption(new QName("id"));
    }

    public Optional<String> connectionTimeoutOption() {
        return element.attributeOption(new QName("connectionTimeout"));
    }

    public Optional<String> maxIdleTimeOption() {
        return element.attributeOption(new QName("maxIdleTime"));
    }

    // In case configuration variables have not yet been resolved

    public Optional<String> agedTimeoutAsStringOption() {
        return element.attributeOption(new QName("agedTimeout"));
    }

    public int agedTimeout() {
        return agedTimeoutAsStringOption()
                .stream()
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(-1);
    }

    public Optional<String> maxPoolSizeAsStringOption() {
        return element.attributeOption(new QName("maxPoolSize"));
    }

    public Optional<String> minPoolSizeAsStringOption() {
        return element.attributeOption(new QName("minPoolSize"));
    }

    public int maxPoolSize() {
        return maxPoolSizeAsStringOption()
                .stream()
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(50);
    }

    public OptionalInt minPoolSizeOption() {
        return minPoolSizeAsStringOption()
                .stream()
                .mapToInt(Integer::parseInt)
                .findFirst();
    }

    public Optional<String> reapTimeOption() {
        return element.attributeOption(new QName("reapTime"));
    }

    public String reapTime() {
        return reapTimeOption().orElse("3m");
    }
}
