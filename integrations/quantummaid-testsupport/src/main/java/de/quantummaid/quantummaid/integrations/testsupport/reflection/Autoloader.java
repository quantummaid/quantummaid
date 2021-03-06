/*
 * Copyright (c) 2020 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.quantummaid.quantummaid.integrations.testsupport.reflection;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static de.quantummaid.httpmaid.util.Validators.validateNotNull;
import static de.quantummaid.httpmaid.util.Validators.validateNotNullNorEmpty;
import static java.lang.Thread.currentThread;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@SuppressWarnings("java:S2658")
@Slf4j
public final class Autoloader {

    private Autoloader() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> load(final String fullyQualifiedClassName, final Class<T> superType) {
        validateNotNullNorEmpty(fullyQualifiedClassName, "fullyQualifiedClassName");
        validateNotNull(superType, "superType");
        return (Optional<T>) loadClass(fullyQualifiedClassName)
                .map(ZeroArgumentConstructorInstantiator::instantiate);
    }

    @SuppressWarnings("unchecked")
    private static <T> Optional<Class<? extends T>> loadClass(final String fullyQualifiedClassName) {
        final ClassLoader classLoader = currentThread().getContextClassLoader();
        try {
            final Class<? extends T> clazz =
                    (Class<? extends T>) classLoader.loadClass(fullyQualifiedClassName);
            return of(clazz);
        } catch (final ClassNotFoundException e) {
            log.trace("Class {} could not been autoloaded", fullyQualifiedClassName, e);
            return empty();
        }
    }
}
