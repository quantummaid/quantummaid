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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestContext {
    private final QuantumMaid quantumMaid;
    private final Integer port;
    private final String host;

    public static TestContext testContext(final QuantumMaid quantumMaid,
                                          final Integer port,
                                          final String host) {
        return new TestContext(quantumMaid, port, host);
    }

    public QuantumMaid quantumMaid() {
        return quantumMaid;
    }

    public Integer port() {
        return port;
    }

    public String host() {
        return host;
    }

    public String url() {
        return format("http://%s:%d/", host, port);
    }
}
