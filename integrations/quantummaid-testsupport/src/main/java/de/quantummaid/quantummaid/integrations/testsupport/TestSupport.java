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

package de.quantummaid.quantummaid.integrations.testsupport;

import de.quantummaid.quantummaid.QuantumMaid;
import de.quantummaid.quantummaid.integrations.testsupport.parameters.Parameters;
import de.quantummaid.quantummaid.integrations.testsupport.reflection.Autoloader;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.quantummaid.quantummaid.integrations.testsupport.FreePortPool.freePort;
import static de.quantummaid.quantummaid.integrations.testsupport.parameters.Parameter.parameter;
import static de.quantummaid.quantummaid.integrations.testsupport.parameters.Parameters.parameters;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestSupport {
    private static final Parameters PARAMETERS = parameters(
            parameter("port", int.class, TestContext::port),
            parameter("host", String.class, TestContext::host),
            parameter("url", String.class, TestContext::url)
    );
    private static final List<String> AUTOLOADABLE_EXTENSIONS = List.of(
            "de.quantummaid.quantummaid.integrations.restassured.RestAssuredExtension"
    );
    private final ThreadLocal<TestContext> testContext = ThreadLocal.withInitial(() -> null);
    private final ThreadLocal<List<TestExtension>> extensions = ThreadLocal.withInitial(Collections::emptyList);

    public static TestSupport testSupport() {
        return new TestSupport();
    }

    public void setExtensions(final List<TestExtension> extensions) {
        setExtensions(extensions, false);
    }

    public void setExtensions(final List<TestExtension> extensions,
                              final boolean autoload) {
        final List<TestExtension> allExtensions = new ArrayList<>(extensions);
        if (autoload) {
            final List<TestExtension> autoloadedTestExtensions = autoloadTestExtensions();
            allExtensions.addAll(autoloadedTestExtensions);
        }
        this.extensions.set(allExtensions);
    }

    public TestContext testContext() {
        return testContext.get();
    }

    public void init(final QuantumMaidProvider provider) {
        close();
        final int port = freePort();
        final String host = "localhost";
        final QuantumMaid quantumMaid = provider.provide(port);
        quantumMaid.runAsynchronously();
        final TestContext testContext = TestContext.testContext(quantumMaid, port, host);
        this.testContext.set(testContext);
        this.extensions.get().forEach(extension -> extension.afterInitialization(testContext));
    }

    public void close() {
        if (testContext() == null) {
            return;
        }
        final QuantumMaid quantumMaid = testContext().quantumMaid();
        quantumMaid.close();
    }

    public boolean supportsParameter(final String name, final Class<?> type) {
        return PARAMETERS.supports(name, type);
    }

    public Object resolveParameter(final String name, final Class<?> type) {
        return PARAMETERS.provide(name, type, testContext());
    }

    private static List<TestExtension> autoloadTestExtensions() {
        return AUTOLOADABLE_EXTENSIONS.stream()
                .map(fullyQualifiedClassName -> Autoloader.load(fullyQualifiedClassName, TestExtension.class))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }
}
