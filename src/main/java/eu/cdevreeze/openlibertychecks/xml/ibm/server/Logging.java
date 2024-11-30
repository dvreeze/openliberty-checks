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

import com.google.common.base.Preconditions;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.ElementTree;

import javax.xml.namespace.QName;
import java.util.Optional;

/**
 * Element named "logging" in a server.xml file.
 *
 * @author Chris de Vreeze
 */
public final class Logging implements ServerXmlContent {

    private final ElementTree.Element element;

    public Logging(ElementTree.Element element) {
        Preconditions.checkArgument(element.elementName().getLocalPart().equals("logging"));
        this.element = element;
    }

    public ElementTree.Element getElement() {
        return element;
    }

    public Optional<String> hideMessageOption() {
        return element.attributeOption(new QName("hideMessage"));
    }

    public Optional<String> traceSpecificationOption() {
        return element.attributeOption(new QName("traceSpecification"));
    }
}
