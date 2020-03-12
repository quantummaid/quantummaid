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
import org.junit.jupiter.api.extension.*;

import static de.quantummaid.quantummaid.integrations.junit5.FreePortPool.freePort;
import static de.quantummaid.quantummaid.integrations.junit5.RestAssuredInitializer.initializeRestAssured;

public final class QuantumMaidTestExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        BeforeAllCallback,
        AfterAllCallback {
    private QuantumMaid quantumMaid;

    @Override
    public void afterAll(final ExtensionContext extensionContext) {
        if (quantumMaid != null) {
            quantumMaid.close();
        }
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) {

    }

    @Override
    public void beforeAll(final ExtensionContext extensionContext) {

    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) {
        if (quantumMaid != null) {
            return;
        }
        final Object testInstance = extensionContext.getRequiredTestInstance();
        if (testInstance instanceof QuantumMaidProvider) {
            final QuantumMaidProvider provider = (QuantumMaidProvider) testInstance;
            final int port = freePort();
            quantumMaid = provider.provide(port);
            quantumMaid.runAsynchronously();
            initializeRestAssured(port);
        }
    }
}
