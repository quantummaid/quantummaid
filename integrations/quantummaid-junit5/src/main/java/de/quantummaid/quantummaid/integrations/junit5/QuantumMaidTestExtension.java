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

import de.quantummaid.quantummaid.QuantumMaid;
import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class QuantumMaidTestExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        BeforeAllCallback,
        InvocationInterceptor,
        AfterAllCallback,
        ParameterResolver {
    private QuantumMaid quantumMaid;

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        if (quantumMaid != null) {
            quantumMaid.close();
        }
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void beforeAll(final ExtensionContext extensionContext) throws Exception {
        final Class<?> testClass = extensionContext.getTestClass().orElseThrow();
        final QuantumMaidTest[] annotationsByType = testClass.getAnnotationsByType(QuantumMaidTest.class);
        final QuantumMaidTest annotation = annotationsByType[0];
        final int port = 10000;

        quantumMaid = provideQuantumMaid(annotation.value(), port);

        quantumMaid.runAsynchronously();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        return false;
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        return null;
    }

    private static QuantumMaid provideQuantumMaid(final Class<?> provider, final int port) {
        final List<Method> providerMethods = Arrays.stream(provider.getMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getReturnType().equals(QuantumMaid.class))
                .filter(method -> method.getParameters().length == 1)
                .filter(method -> method.getParameters()[0].getType().equals(int.class))
                .collect(Collectors.toList());
        if (providerMethods.size() != 1) {
            throw new UnsupportedOperationException(providerMethods.toString());
        }
        final Method method = providerMethods.get(0);
        try {
            return (QuantumMaid) method.invoke(null, port);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
