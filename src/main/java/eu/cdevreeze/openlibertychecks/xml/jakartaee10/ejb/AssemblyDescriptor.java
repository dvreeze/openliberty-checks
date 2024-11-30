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
import com.google.common.collect.ImmutableList;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.Names;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.SecurityRole;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementTree;

import javax.xml.namespace.QName;
import java.util.Optional;

import static eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementPredicates.hasName;

/**
 * Assembly descriptor XML element wrapper.
 *
 * @author Chris de Vreeze
 */
public final class AssemblyDescriptor implements EjbJarXmlContent {

    private final ElementTree.Element element;

    public AssemblyDescriptor(ElementTree.Element element) {
        Preconditions.checkArgument(Names.JAKARTAEE_NS.equals(element.elementName().getNamespaceURI()));
        Preconditions.checkArgument(element.elementName().getLocalPart().equals("assembly-descriptor"));

        this.element = element;
    }

    public ElementTree.Element getElement() {
        return element;
    }

    public Optional<String> idOption() {
        return element.attributeOption(new QName("id"));
    }

    public ImmutableList<SecurityRole> securityRoles() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "security-role"))
                .map(SecurityRole::new)
                .collect(ImmutableList.toImmutableList());
    }

    public ImmutableList<MethodPermission> methodPermissions() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "method-permission"))
                .map(MethodPermission::new)
                .collect(ImmutableList.toImmutableList());
    }

    public Optional<ExcludeList> excludeListOption() {
        String ns = element.elementName().getNamespaceURI();
        return element.childElementStream(hasName(ns, "exclude-list"))
                .map(ExcludeList::new)
                .findFirst();
    }
}
