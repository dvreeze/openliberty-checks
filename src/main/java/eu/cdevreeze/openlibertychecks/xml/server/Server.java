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

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementPredicates.hasName;

/**
 * Root element of an OpenLiberty server.xml file.
 * <p>
 * Note that a server.xml file may contain configuration variables.
 *
 * @author Chris de Vreeze
 */
public final class Server {

    private final ElementTree.Element element;

    public Server(ElementTree.Element element) {
        Preconditions.checkArgument(element.elementName().getLocalPart().equals("server"));
        this.element = element;
    }

    public ElementTree.Element getElement() {
        return element;
    }

    public Optional<String> descriptionOption() {
        return element.attributeOption(new QName("description"));
    }

    public ImmutableList<String> features() {
        return element.childElementStream(hasName("featureManager"))
                .flatMap(e -> e.childElementStream(hasName("feature")))
                .map(ElementTree.Element::text)
                .map(String::strip)
                .collect(ImmutableList.toImmutableList());
    }

    public ImmutableList<HttpEndpoint> httpEndpoints() {
        return element.childElementStream(hasName("httpEndpoint"))
                .map(HttpEndpoint::new)
                .collect(ImmutableList.toImmutableList());
    }

    public Optional<ApplicationManager> applicationManagerOption() {
        return element.childElementStream(hasName("applicationManager"))
                .map(ApplicationManager::new)
                .findFirst();
    }

    public ImmutableList<JndiEntry> jndiEntries() {
        return element.childElementStream(hasName("jndiEntry"))
                .map(JndiEntry::new)
                .collect(ImmutableList.toImmutableList());
    }

    // TODO
}
