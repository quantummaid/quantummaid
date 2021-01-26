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

package de.quantummaid.quantummaid;

import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.httpmaid.client.HttpMaidClient;
import de.quantummaid.httpmaid.client.SimpleHttpResponseObject;
import de.quantummaid.quantummaid.usecases.ComplexUseCase;
import org.junit.jupiter.api.Test;

import static de.quantummaid.httpmaid.HttpMaid.anHttpMaid;
import static de.quantummaid.httpmaid.client.HttpClientRequest.aGetRequestToThePath;
import static de.quantummaid.httpmaid.client.HttpMaidClient.aHttpMaidClientBypassingRequestsDirectlyTo;
import static de.quantummaid.httpmaid.exceptions.ExceptionConfigurators.toMapExceptionsOfType;
import static de.quantummaid.quantummaid.PortUtils.assertPortIsClosed;
import static de.quantummaid.quantummaid.PortUtils.waitForPortToBeAvailable;
import static de.quantummaid.quantummaid.QuantumMaid.quantumMaid;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public final class QuantumMaidSpecs {

    @Test
    public void quantumMaidCanInstantiateComplexUseCases() {
        final QuantumMaid quantumMaid = quantumMaid()
                .get("/", ComplexUseCase.class);
        final HttpMaid httpMaid = quantumMaid.httpMaid();
        final HttpMaidClient client = aHttpMaidClientBypassingRequestsDirectlyTo(httpMaid)
                .build();
        final SimpleHttpResponseObject response = client.issue(aGetRequestToThePath("/"));
        assertThat(response.getBody(), is("\"abc\""));
    }

    @Test
    public void quantumMaidCanConfigureHttpMaid() {
        final QuantumMaid quantumMaid = quantumMaid()
                .get("/", (request, response) -> {
                    throw new UnsupportedOperationException();
                })
                .configured(toMapExceptionsOfType(UnsupportedOperationException.class,
                        (exception, request, response) -> response.setBody("foo")));
        final HttpMaid httpMaid = quantumMaid.httpMaid();
        final HttpMaidClient client = aHttpMaidClientBypassingRequestsDirectlyTo(httpMaid)
                .build();
        final SimpleHttpResponseObject response = client.issue(aGetRequestToThePath("/"));
        assertThat(response.getBody(), is("foo"));
    }

    @Test
    public void quantumMaidClosesHttpMaidOnExit() {
        final int port = 1337;
        for (int i = 0; i < 3; ++i) {
            final QuantumMaid quantumMaid = quantumMaid()
                    .get("/", (request, response) -> response.setBody("I'm up"))
                    .withLocalHostEndpointOnPort(port);
            quantumMaid.runAsynchronously();
            waitForPortToBeAvailable(port);
            quantumMaid.close();
            PortUtils.waitForPortToClose(port);
            assertPortIsClosed(port);
        }
    }

    @SuppressWarnings("removal")
    @Test
    public void anHttpMaidInstanceCannotBeBeRegisteredToMoreThanOneQuantumMaidInstance() {
        final HttpMaid httpMaid = anHttpMaid().build();
        final QuantumMaid quantumMaid1 = quantumMaid().withHttpMaid(httpMaid);
        assertThat(quantumMaid1, notNullValue());
        QuantumMaid quantumMaid2 = null;
        HttpMaidAlreadyRegisteredException exception = null;
        try {
            quantumMaid2 = quantumMaid().withHttpMaid(httpMaid);
        } catch (final HttpMaidAlreadyRegisteredException e) {
            exception = e;
        }
        assertThat(quantumMaid2, nullValue());
        assertThat(exception, notNullValue());
        assertThat(exception.getMessage(), is("HttpMaid instance has already been registered to a QuantumMaid instance. " +
                "It cannot be registered to more than one QuantumMaid instance."));
    }
}
