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
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.Names;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementTree;

import javax.xml.namespace.QName;
import java.util.Optional;

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementPredicates.hasName;

/**
 * EJB Jar XML element wrapper. Corresponds to the contents of a beans.xml file.
 *
 * @author Chris de Vreeze
 */
public final class Beans implements BeansXmlContent {

    private final ElementTree.Element element;

    public Beans(ElementTree.Element element) {
        Preconditions.checkArgument(Names.JAKARTAEE_NS.equals(element.elementName().getNamespaceURI()));
        Preconditions.checkArgument(element.elementName().getLocalPart().equals("beans"));

        this.element = element;
    }

    public ElementTree.Element getElement() {
        return element;
    }

    public BeanDiscoveryMode beanDiscoveryMode() {
        return element.attributeOption(new QName("bean-discovery-mode"))
                .map(BeanDiscoveryMode::valueOf)
                .orElse(BeanDiscoveryMode.annotated);
    }

    public Optional<Interceptors> interceptorsElementOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "interceptors"))
                .map(Interceptors::new)
                .findFirst();
    }

    public Optional<Decorators> decoratorsElementOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "decorators"))
                .map(Decorators::new)
                .findFirst();
    }

    public Optional<Alternatives> alternativesElementOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "alternatives"))
                .map(Alternatives::new)
                .findFirst();
    }

    public Optional<Scan> scanOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "scan"))
                .map(Scan::new)
                .findFirst();
    }

    public Optional<String> trimOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "trim"))
                .map(ElementTree.Element::text)
                .findFirst();
    }
}
