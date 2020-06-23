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

import de.quantummaid.httpmaid.client.HttpMaidClient;
import de.quantummaid.quantummaid.QuantumMaid;
import de.quantummaid.quantummaid.integrations.testsupport.QuantumMaidProvider;
import org.junit.jupiter.api.Test;

import static de.quantummaid.httpmaid.client.HttpClientRequest.aGetRequestToThePath;
import static de.quantummaid.httpmaid.client.HttpMaidClient.aHttpMaidClientForTheHost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@QuantumMaidTest(ExternalProvider.class)
public final class InterfaceInTestClassTakesPrecedenceSpecs implements QuantumMaidProvider {

    @Override
    public QuantumMaid provide(final int port) {
        return QuantumMaid.quantumMaid()
                .get("/", (request, response) -> response.setBody("from internal provider"))
                .withLocalHostEndpointOnPort(port);
    }

    @Test
    public void interfaceInTestClassTakesPrecedence(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("from internal provider"));
    }
}
