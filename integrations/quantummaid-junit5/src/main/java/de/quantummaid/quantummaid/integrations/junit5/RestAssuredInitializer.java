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

package de.quantummaid.quantummaid.integrations.junit5;

import java.lang.reflect.Field;
import java.util.Optional;

final class RestAssuredInitializer {
    private static final String REST_ASSURED = "io.restassured.RestAssured";

    private RestAssuredInitializer() {
    }

    static void initializeRestAssured(final int port) {
        final Optional<Class<?>> type = findClass(REST_ASSURED);
        if(type.isEmpty()) {
            return;
        }
        final Class<?> restAssuredClass = type.get();
        setStaticField("port", port, restAssuredClass);
    }

    private static Optional<Class<?>> findClass(final String fullyQualifiedClassName) {
        try {
            final Class<?> clazz = Class.forName(fullyQualifiedClassName);
            return Optional.of(clazz);
        } catch (final ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private static void setStaticField(final String name, final Object value, final Class<?> clazz) {
        try {
            final Field field = clazz.getField(name);
            field.set(null, value);
        } catch (final NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
