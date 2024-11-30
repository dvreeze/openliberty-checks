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

package eu.cdevreeze.openlibertychecks.xml.jakartaee10.cdi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.Names;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementTree;

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementPredicates.hasName;

/**
 * Alternatives XML element wrapper.
 *
 * @author Chris de Vreeze
 */
public final class Alternatives implements BeansXmlContent {

    private final ElementTree.Element element;

    public Alternatives(ElementTree.Element element) {
        Preconditions.checkArgument(Names.JAKARTAEE_NS.equals(element.elementName().getNamespaceURI()));
        Preconditions.checkArgument(element.elementName().getLocalPart().equals("alternatives"));

        this.element = element;
    }

    public ElementTree.Element getElement() {
        return element;
    }

    public ImmutableList<String> classes() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "class"))
                .map(ElementTree.Element::text)
                .collect(ImmutableList.toImmutableList());
    }

    public ImmutableList<String> stereotypes() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "stereotype"))
                .map(ElementTree.Element::text)
                .collect(ImmutableList.toImmutableList());
    }
}
