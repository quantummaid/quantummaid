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

import de.quantummaid.quantummaid.integrations.testsupport.reflection.ZeroArgumentConstructorInstantiator;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ParameterResolutionSpecs {

    @Test
    public void parameterWithUnknownNameIsNotSupported() {
        final boolean supported = TestSupport.supportsParameter("foo", String.class);
        assertThat(supported, is(false));
    }

    @Test
    public void parameterWithUnknownTypeIsNotSupported() {
        final boolean supported = TestSupport.supportsParameter("port", Boolean.class);
        assertThat(supported, is(false));
    }

    @Test
    public void testClassWithoutConstructor() {
        QuantumMaidTestException exception = null;
        try {
            ZeroArgumentConstructorInstantiator.instantiate(int.class);
        } catch (final QuantumMaidTestException e) {
            exception = e;
        }
        assertThat(exception, is(notNullValue()));
        assertThat(exception.getMessage(), is("class 'int' has to provide a zero-parameter constructor in" +
                " order to be usable in QuantumMaid tests"));
    }

    @Test
    public void testClassWithoutPermissionToCallConstructor() {
        QuantumMaidTestException exception = null;
        try {
            ZeroArgumentConstructorInstantiator.instantiate(InputStream.class);
        } catch (final QuantumMaidTestException e) {
            exception = e;
        }
        assertThat(exception, is(notNullValue()));
        assertThat(exception.getMessage(), is("cannot instantiate class 'InputStream'"));
    }
}
