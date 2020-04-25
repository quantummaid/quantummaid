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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static de.quantummaid.quantummaid.integrations.testsupport.QuantumMaidTestException.quantumMaidTestException;
import static java.lang.String.format;

public final class ZeroArgumentConstructorInstantiator {

    private ZeroArgumentConstructorInstantiator() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T instantiate(final Class<? extends T> providerClass) {
        final Constructor<?> zeroArgsConstructor = findZeroArgsConstructor(providerClass);
        try {
            return (T) zeroArgsConstructor.newInstance();
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw quantumMaidTestException(format("cannot instantiate class '%s'", providerClass.getSimpleName()), e);
        }
    }

    private static Constructor<?> findZeroArgsConstructor(final Class<?> clazz) {
        try {
            return clazz.getConstructor();
        } catch (final NoSuchMethodException e) {
            throw quantumMaidTestException(format("class '%s' has to provide a zero-parameter constructor " +
                            "in order to be usable in QuantumMaid tests", clazz.getSimpleName()));
        }
    }
}
