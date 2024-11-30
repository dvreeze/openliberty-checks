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

package eu.cdevreeze.openlibertychecks.xml.jakartaee10;

import com.google.common.collect.ImmutableList;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.ejb.AssemblyDescriptor;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.ejb.EjbJar;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.ejb.SessionBean;
import eu.cdevreeze.openlibertychecks.xml.jakartaee10.ejb.SessionType;
import eu.cdevreeze.yaidom4j.dom.ancestryaware.Document;
import eu.cdevreeze.yaidom4j.dom.immutabledom.jaxpinterop.DocumentParsers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ejb-jar dialect tests.
 * <p>
 * This is not a regular unit test.
 *
 * @author Chris de Vreeze
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EjbJarDialectTests {

    private Document parseDocument() {
        InputStream inputStream = EjbJarDialectTests.class.getResourceAsStream("/dialects/sample-ejb-jar.xml");
        return Document.from(
                DocumentParsers.builder().removingInterElementWhitespace().build()
                        .parse(new InputSource(inputStream))
        );
    }

    @Test
    public void testEjbJarXmlParsing() {
        Document doc = parseDocument();

        EjbJar ejbJar = new EjbJar(doc.documentElement());

        String ns = ejbJar.getElement().elementName().getNamespaceURI();
        assertEquals(Names.JAKARTAEE_NS, ns);

        ImmutableList<SessionBean> sessionBeans =
                ejbJar.enterpriseBeansElementOption().stream()
                        .flatMap(e -> e.sessionBeans().stream())
                        .collect(ImmutableList.toImmutableList());

        assertEquals(1, sessionBeans.size());

        SessionBean sessionBean = sessionBeans.get(0);

        assertEquals("DDBasedSLSB", sessionBean.ejbName());

        assertEquals(
                ImmutableList.of("org.jboss.as.test.integration.ejb.security.FullAccess"),
                sessionBean.businessLocals()
        );

        assertTrue(sessionBean.localBeanOption().isPresent());

        assertEquals(
                Optional.of("org.jboss.as.test.integration.ejb.security.DDBasedSLSB"),
                sessionBean.ejbClassOption()
        );

        assertEquals(
                Optional.of(SessionType.Stateless),
                sessionBean.sessionTypeOption()
        );

        assertEquals(
                List.of("DDRole1", "DDRole2", "DDRole3"),
                sessionBean.securityRoleRefs().stream()
                        .map(SecurityRoleRef::roleName)
                        .toList()
        );

        Optional<AssemblyDescriptor> assemblyDescriptorOption =
                ejbJar.assemblyDescriptorOption();

        assertTrue(assemblyDescriptorOption.isPresent());

        AssemblyDescriptor assemblyDescriptor = assemblyDescriptorOption.orElseThrow();

        assertEquals(
                List.of("TestRole"),
                assemblyDescriptor.methodPermissions().stream()
                        .flatMap(e -> e.roleNames().stream())
                        .toList()
        );

        assertEquals(
                List.of(
                        List.of("DDBasedSLSB", "onlyTestRoleCanAccess")
                ),
                assemblyDescriptor.methodPermissions().stream()
                        .flatMap(e -> e.methods().stream())
                        .map(e -> List.of(e.ejbName(), e.methodName()))
                        .toList()
        );

        assertEquals(
                List.of(
                        List.of("DDBasedSLSB", "accessDenied")
                ),
                assemblyDescriptor.excludeListOption().stream()
                        .flatMap(e -> e.methods().stream())
                        .map(e -> List.of(e.ejbName(), e.methodName()))
                        .toList()
        );
    }
}
