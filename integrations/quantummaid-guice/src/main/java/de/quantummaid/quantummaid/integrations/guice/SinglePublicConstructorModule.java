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

package de.quantummaid.quantummaid.integrations.guice;

import com.google.inject.AbstractModule;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.util.List;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class SinglePublicConstructorModule<T> extends AbstractModule {
    private final Class<T> type;
    private final Constructor<T> constructor;

    static <T> AbstractModule singlePublicConstructorModule(final Class<T> type) {
        final List<Constructor<T>> constructors = publicConstructors(type);
        if (constructors.size() == 1) {
            final Constructor<T> constructor = constructors.get(0);
            return new SinglePublicConstructorModule<>(type, constructor);
        } else {
            final String constructorsString = constructors.stream()
                    .map(Constructor::toGenericString)
                    .collect(joining(", ", "[", "]"));
            throw new UnsupportedOperationException(format(
                    "Can only bind classes that have exactly one public constructor. Class '%s' has the following constructors: %s",
                    type.getName(), constructorsString));
        }
    }

    @Override
    protected void configure() {
        bind(type).toConstructor(constructor);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<Constructor<T>> publicConstructors(final Class<T> type) {
        final Constructor<T>[] constructors = (Constructor<T>[]) type.getConstructors();
        return stream(constructors)
                .filter(constructor -> isPublic(constructor.getModifiers()))
                .collect(toList());
    }
}
