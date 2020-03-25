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

import java.util.function.Function;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Parameter {
    private final String name;
    private final Class<?> type;
    private final Function<TestContext, Object> provider;

    @SuppressWarnings("unchecked")
    public static <T> Parameter parameter(final String name, final Class<T> type, final Function<TestContext, T> provider) {
        return new Parameter(name, type, (Function<TestContext, Object>) provider);
    }

    public boolean supports(final String name, final Class<?> type) {
        return this.name.equals(name) && this.type.equals(type);
    }

    public Object provide(final TestContext testContext) {
        return provider.apply(testContext);
    }
}
