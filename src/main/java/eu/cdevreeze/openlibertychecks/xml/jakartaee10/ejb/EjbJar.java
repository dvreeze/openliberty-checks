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

import java.util.Optional;

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementPredicates.hasName;

/**
 * EJB Jar XML element wrapper. Corresponds to the contents of an ejb-jar.xml file.
 * <p>
 * Note that this ejb-jar element is only used for standalone ejb-jars and for ejb-jars in an EAR file.
 * Inside a WAR file, this content is ignored.
 *
 * @author Chris de Vreeze
 */
public final class EjbJar implements EjbJarXmlContent {

    private final ElementTree.Element element;

    public EjbJar(ElementTree.Element element) {
        Preconditions.checkArgument(Names.JAKARTAEE_NS.equals(element.elementName().getNamespaceURI()));
        Preconditions.checkArgument(element.elementName().getLocalPart().equals("ejb-jar"));

        this.element = element;
    }

    public ElementTree.Element getElement() {
        return element;
    }

    public Optional<EnterpriseBeans> enterpriseBeansElementOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "enterprise-beans"))
                .map(EnterpriseBeans::new)
                .findFirst();
    }

    public Optional<Interceptors> interceptorsElementOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "interceptors"))
                .map(Interceptors::new)
                .findFirst();
    }

    public Optional<Relationships> relationshipsElementOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "relationships"))
                .map(Relationships::new)
                .findFirst();
    }

    public Optional<AssemblyDescriptor> assemblyDescriptorOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "assembly-descriptor"))
                .map(AssemblyDescriptor::new)
                .findFirst();
    }

    public Optional<String> ejbClientJarOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "ejb-client-jar"))
                .map(ElementTree.Element::text)
                .findFirst();
    }
}
