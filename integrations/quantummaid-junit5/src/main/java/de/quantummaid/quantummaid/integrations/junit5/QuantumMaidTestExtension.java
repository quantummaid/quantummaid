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

import de.quantummaid.quantummaid.integrations.testsupport.QuantumMaidProvider;
import de.quantummaid.quantummaid.integrations.testsupport.TestExtension;
import de.quantummaid.quantummaid.integrations.testsupport.TestSupport;
import de.quantummaid.quantummaid.integrations.testsupport.reflection.ZeroArgumentConstructorInstantiator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import static de.quantummaid.quantummaid.integrations.testsupport.QuantumMaidTestException.quantumMaidTestException;
import static de.quantummaid.quantummaid.integrations.testsupport.TestSupport.testSupport;
import static de.quantummaid.quantummaid.integrations.testsupport.reflection.ZeroArgumentConstructorInstantiator.instantiate;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public final class QuantumMaidTestExtension implements
        BeforeAllCallback,
        BeforeEachCallback,
        AfterAllCallback,
        ParameterResolver {
    private final TestSupport testSupport = testSupport();
    private QuantumMaidTest annotation;

    @Override
    public void beforeAll(final ExtensionContext context) {
        annotation = findTestAnnotation(context);
        final List<TestExtension> extensions = Arrays.stream(annotation.extensions())
                .map(ZeroArgumentConstructorInstantiator::instantiate)
                .collect(toList());
        testSupport.setExtensions(extensions);
        final boolean autoloadExtensions = annotation.autoloadExtensions();
        if (autoloadExtensions) {
            testSupport.autoloadExtensions();
        }
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) {
        final QuantumMaidProvider provider = determineProvider(extensionContext);
        testSupport.init(provider);
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) {
        testSupport.close();
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext) {
        final String name = name(parameterContext);
        final Class<?> type = type(parameterContext);
        return TestSupport.supportsParameter(name, type);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext) {
        final String name = name(parameterContext);
        final Class<?> type = type(parameterContext);
        return testSupport.resolveParameter(name, type);
    }

    private QuantumMaidProvider determineProvider(final ExtensionContext extensionContext) {
        final Object testInstance = extensionContext.getRequiredTestInstance();
        if (testInstance instanceof QuantumMaidProvider) {
            return (QuantumMaidProvider) testInstance;
        } else {
            final Class<? extends QuantumMaidProvider> providerClass = annotation.value();
            if (providerClass.equals(QuantumMaidProvider.class)) {
                throw quantumMaidTestException("unable to find QuantumMaid provider");
            }
            return instantiate(providerClass);
        }
    }

    private static QuantumMaidTest findTestAnnotation(final ExtensionContext extensionContext) {
        final Class<?> testClass = extensionContext.getTestClass()
                .orElseThrow(() -> quantumMaidTestException("test class missing"));
        final QuantumMaidTest[] annotationsByType = testClass.getAnnotationsByType(QuantumMaidTest.class);
        if (annotationsByType.length > 1) {
            throw quantumMaidTestException(format(
                    "multiple occurrences of annotation %s", QuantumMaidTest.class.getSimpleName()));
        }
        return annotationsByType[0];
    }

    private static Class<?> type(final ParameterContext parameterContext) {
        final Parameter parameter = parameterContext.getParameter();
        return parameter.getType();
    }

    private static String name(final ParameterContext parameterContext) {
        final Parameter parameter = parameterContext.getParameter();
        return parameter.getName();
    }
}
