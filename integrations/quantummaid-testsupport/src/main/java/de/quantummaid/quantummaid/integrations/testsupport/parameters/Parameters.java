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

package de.quantummaid.quantummaid.integrations.testsupport.parameters;

import de.quantummaid.quantummaid.integrations.testsupport.TestContext;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.quantummaid.integrations.testsupport.QuantumMaidTestException.quantumMaidTestException;
import static java.util.Arrays.asList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Parameters {
    private final List<Parameter> parameters;

    public static Parameters parameters(final Parameter... parameters) {
        return new Parameters(asList(parameters));
    }

    public boolean supports(final String name, final Class<?> type) {
        final Optional<Parameter> parameter = matchingParameter(name, type);
        return parameter.isPresent();
    }

    public Object provide(final String name,
                          final Class<?> type,
                          final TestContext testContext) {
        final Parameter parameter = matchingParameter(name, type)
                .orElseThrow(() -> quantumMaidTestException("This should never happen"));
        return parameter.provide(testContext);
    }

    private Optional<Parameter> matchingParameter(final String name, final Class<?> type) {
        return this.parameters.stream()
                .filter(parameter -> parameter.supports(name, type))
                .findFirst();
    }
}
