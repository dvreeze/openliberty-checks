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

package eu.cdevreeze.openlibertychecks.reflection.jakartaee10.servlet;

import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRequestAttributeListener;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionIdListener;
import jakarta.servlet.http.HttpSessionListener;

import java.util.Optional;
import java.util.stream.Stream;

import static eu.cdevreeze.openlibertychecks.reflection.internal.AnnotationSupport.findAnnotation;
import static eu.cdevreeze.openlibertychecks.reflection.internal.AnnotationSupport.findDeclaredAnnotation;

/**
 * Servlet annotation support (for annotations having retention "runtime").
 *
 * @author Chris de Vreeze
 */
public class ServletAnnotations {

    private ServletAnnotations() {
    }

    public static boolean isWebServlet(Class<?> clazz) {
        return findWebServletAnnotation(clazz).isPresent();
    }

    public static Optional<WebServlet> findWebServletAnnotation(Class<?> clazz) {
        return findDeclaredAnnotation(clazz, WebServlet.class);
    }

    public static boolean isWebFilter(Class<?> clazz) {
        return findWebFilterAnnotation(clazz).isPresent();
    }

    public static Optional<WebFilter> findWebFilterAnnotation(Class<?> clazz) {
        return findDeclaredAnnotation(clazz, WebFilter.class);
    }

    public static boolean isWebListener(Class<?> clazz) {
        return findWebListenerAnnotation(clazz).isPresent();
    }

    public static boolean isAllowedWebListener(Class<?> clazz) {
        return isWebListener(clazz) && canBeWebListener(clazz);
    }

    public static Optional<WebListener> findWebListenerAnnotation(Class<?> clazz) {
        return findDeclaredAnnotation(clazz, WebListener.class);
    }

    public static Optional<WebListener> findAllowedWebListenerAnnotation(Class<?> clazz) {
        return findWebListenerAnnotation(clazz).filter(ignored -> canBeWebListener(clazz));
    }

    public static boolean declaresServletSecurity(Class<?> clazz) {
        return findServletSecurityAnnotation(clazz).isPresent();
    }

    public static Optional<ServletSecurity> findServletSecurityAnnotation(Class<?> clazz) {
        return findAnnotation(clazz, ServletSecurity.class); // can be inherited
    }

    private static boolean canBeWebListener(Class<?> clazz) {
        return Stream.of(
                        ServletContextListener.class,
                        ServletContextAttributeListener.class,
                        ServletRequestListener.class,
                        ServletRequestAttributeListener.class,
                        HttpSessionListener.class,
                        HttpSessionAttributeListener.class,
                        HttpSessionIdListener.class
                )
                .anyMatch(c -> c.isAssignableFrom(clazz));
    }
}
