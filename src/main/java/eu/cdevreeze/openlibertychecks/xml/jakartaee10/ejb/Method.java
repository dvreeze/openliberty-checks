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

package eu.cdevreeze.openlibertychecks.xml.jakartaee10.ejb;

import com.google.common.base.Preconditions;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.Names;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementTree;

import javax.xml.namespace.QName;
import java.util.Optional;

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementPredicates.hasName;

/**
 * Method XML element wrapper.
 *
 * @author Chris de Vreeze
 */
public final class Method implements EjbJarXmlContent {

    private final ElementTree.Element element;

    public Method(ElementTree.Element element) {
        Preconditions.checkArgument(Names.JAKARTAEE_NS.equals(element.elementName().getNamespaceURI()));
        Preconditions.checkArgument(element.elementName().getLocalPart().equals("method"));

        this.element = element;
    }

    public ElementTree.Element getElement() {
        return element;
    }

    public Optional<String> idOption() {
        return element.attributeOption(new QName("id"));
    }

    public String ejbName() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "ejb-name"))
                .findFirst()
                .orElseThrow()
                .text();
    }

    public Optional<MethodIntfType> methodIntfTypeOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "method-intf"))
                .findFirst()
                .map(ElementTree.Element::text)
                .map(MethodIntfType::valueOf);
    }

    public String methodName() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "method-name"))
                .findFirst()
                .orElseThrow()
                .text();
    }

    public Optional<MethodParams> methodParamsElementOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "method-params"))
                .findFirst()
                .map(MethodParams::new);
    }
}