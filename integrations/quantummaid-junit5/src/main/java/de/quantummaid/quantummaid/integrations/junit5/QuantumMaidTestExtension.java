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
import de.quantummaid.quantummaid.integrations.junit5.parameters.Parameters;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Parameter;

import static de.quantummaid.quantummaid.integrations.junit5.FreePortPool.freePort;
import static de.quantummaid.quantummaid.integrations.junit5.RestAssuredInitializer.initializeRestAssured;
import static de.quantummaid.quantummaid.integrations.junit5.TestContext.testContext;
import static de.quantummaid.quantummaid.integrations.junit5.parameters.Parameter.parameter;
import static de.quantummaid.quantummaid.integrations.junit5.parameters.Parameters.parameters;

public final class QuantumMaidTestExtension implements
        BeforeEachCallback,
        AfterAllCallback,
        ParameterResolver {
    private static final Parameters PARAMETERS = parameters(
            parameter("port", int.class, TestContext::port),
            parameter("host", String.class, TestContext::host),
            parameter("url", String.class, TestContext::url)
    );

    private final TestContext testContext = testContext();

    @Override
    public void afterAll(final ExtensionContext extensionContext) {
        final QuantumMaid quantumMaid = testContext.quantumMaid();
        if (quantumMaid != null) {
            quantumMaid.close();
        }
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) {
        if (testContext.quantumMaid() != null) {
            return;
        }
        final Object testInstance = extensionContext.getRequiredTestInstance();
        if (testInstance instanceof QuantumMaidProvider) {
            final QuantumMaidProvider provider = (QuantumMaidProvider) testInstance;
            final int port = freePort();
            final String host = "localhost";
            final QuantumMaid quantumMaid = provider.provide(port);
            quantumMaid.runAsynchronously();
            testContext.update(quantumMaid, port, host);
            initializeRestAssured(port);
        }
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext) throws ParameterResolutionException {
        final String name = name(parameterContext);
        final Class<?> type = type(parameterContext);
        return PARAMETERS.supports(name, type);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext) throws ParameterResolutionException {
        final String name = name(parameterContext);
        final Class<?> type = type(parameterContext);
        return PARAMETERS.provide(name, type, testContext);
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
