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

package eu.cdevreeze.openlibertychecks.reflection.jakartaee10.ejb;

import jakarta.ejb.*;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * EJB annotation support (for annotations having retention "runtime").
 *
 * @author Chris de Vreeze
 */
public class EjbAnnotations {

    private EjbAnnotations() {
    }

    public static boolean isStatelessSessionBean(Class<?> clazz) {
        return findStatelessAnnotation(clazz).isPresent();
    }

    public static Optional<Stateless> findStatelessAnnotation(Class<?> clazz) {
        return Optional.ofNullable(clazz.getDeclaredAnnotation(Stateless.class));
    }

    public static boolean isStatefulSessionBean(Class<?> clazz) {
        return findStatefulAnnotation(clazz).isPresent();
    }

    public static Optional<Stateful> findStatefulAnnotation(Class<?> clazz) {
        return Optional.ofNullable(clazz.getDeclaredAnnotation(Stateful.class));
    }

    public static boolean isSingletonSessionBean(Class<?> clazz) {
        return findSingletonAnnotation(clazz).isPresent();
    }

    public static Optional<Singleton> findSingletonAnnotation(Class<?> clazz) {
        return Optional.ofNullable(clazz.getDeclaredAnnotation(Singleton.class));
    }

    public static boolean isMessageDrivenBean(Class<?> clazz) {
        return findMessageDrivenAnnotation(clazz).isPresent();
    }

    public static Optional<MessageDriven> findMessageDrivenAnnotation(Class<?> clazz) {
        return Optional.ofNullable(clazz.getDeclaredAnnotation(MessageDriven.class));
    }

    public static boolean hasSchedulesAnnotation(Method method) {
        return findSchedulesAnnotation(method).isPresent();
    }

    public static Optional<Schedules> findSchedulesAnnotation(Method method) {
        return Optional.ofNullable(method.getDeclaredAnnotation(Schedules.class));
    }

    public static boolean hasScheduleAnnotation(Method method) {
        return findScheduleAnnotation(method).isPresent();
    }

    public static Optional<Schedule> findScheduleAnnotation(Method method) {
        return Optional.ofNullable(method.getDeclaredAnnotation(Schedule.class));
    }
}
