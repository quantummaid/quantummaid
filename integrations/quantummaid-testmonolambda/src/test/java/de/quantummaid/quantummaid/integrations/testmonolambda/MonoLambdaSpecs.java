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

package de.quantummaid.quantummaid.integrations.testmonolambda;

import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.httpmaid.client.HttpMaidClient;
import de.quantummaid.httpmaid.client.SimpleHttpResponseObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.quantummaid.httpmaid.client.HttpClientRequest.aGetRequestToThePath;
import static de.quantummaid.httpmaid.client.HttpMaidClient.aHttpMaidClientBypassingRequestsDirectlyTo;
import static de.quantummaid.httpmaid.websockets.authorization.AuthorizationDecision.fail;
import static de.quantummaid.injectmaid.api.ReusePolicy.EAGER_SINGLETON;
import static de.quantummaid.quantummaid.integrations.testmonolambda.TestMonoLambda.aTestMonoLambda;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class MonoLambdaSpecs {

    @Test
    public void testMonoLambda() {
        final TestMonoLambda monoLambda = aTestMonoLambda()
                .withHttpMaid(httpMaidBuilder -> httpMaidBuilder
                        .get("/", (request, response) -> response.setBody("foo"))
                )
                .build();
        final HttpMaid httpMaid = monoLambda.httpMaid();

        final HttpMaidClient client = aHttpMaidClientBypassingRequestsDirectlyTo(httpMaid).build();

        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("foo"));
    }

    @Test
    public void testMonoLambdaAutoregistersUseCasesInInjectMaid() {
        final TestMonoLambda monoLambda = aTestMonoLambda()
                .withHttpMaid(httpMaidBuilder -> httpMaidBuilder
                        .get("/", FooUseCase.class)
                )
                .build();
        final HttpMaid httpMaid = monoLambda.httpMaid();
        final HttpMaidClient client = aHttpMaidClientBypassingRequestsDirectlyTo(httpMaid).build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("\"foo\""));
    }

    @Test
    public void testMonoLambdaCanHaveCustomUseCaseRegistrations() {
        final TestMonoLambda monoLambda = aTestMonoLambda()
                .withHttpMaid(httpMaidBuilder -> httpMaidBuilder
                        .get("/", FooUseCase.class)
                )
                .withInjectMaid(injectMaidBuilder -> injectMaidBuilder.withType(FooUseCase.class, EAGER_SINGLETON))
                .build();
        final HttpMaid httpMaid = monoLambda.httpMaid();
        final HttpMaidClient client = aHttpMaidClientBypassingRequestsDirectlyTo(httpMaid).build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("\"foo\""));
    }

    @Test
    public void testMonoLambdaCanHaveAdditionalWebsocketDataProvider() {
        aTestMonoLambda()
                .withAdditionalWebsocketDataProvider(request -> Map.of())
                .build();
    }

    @Test
    public void testMonoLambdaCanHaveWebsocketAuthorizer() {
        aTestMonoLambda()
                .withWebsocketAuthorizer(request -> fail())
                .build();
    }

    @Test
    public void testMonoLambdaCanConnectClient() {
        try (TestMonoLambda testMonoLambda = aTestMonoLambda()
                .withHttpMaid(builder -> builder.get("/", (request, response) -> response.setBody("abc")))
                .build()) {
            try (HttpMaidClient client = testMonoLambda.connectClient()) {
                final SimpleHttpResponseObject response = client.issue(aGetRequestToThePath("/"));
                assertThat(response.getBody(), is("abc"));
            }
        }
    }
}
