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

package de.quantummaid.quantummaid.integrations.monolambda;

import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.httpmaid.client.HttpMaidClient;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.quantummaid.httpmaid.client.HttpClientRequest.aGetRequestToThePath;
import static de.quantummaid.httpmaid.client.HttpMaidClient.aHttpMaidClientBypassingRequestsDirectlyTo;
import static de.quantummaid.httpmaid.http.Http.StatusCodes.OK;
import static de.quantummaid.injectmaid.api.ReusePolicy.EAGER_SINGLETON;
import static de.quantummaid.quantummaid.integrations.monolambda.MonoLambda.aMonoLambdaInRegion;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class MonoLambdaSpecs {

    @Test
    public void monoLambda() {
        final MonoLambda monoLambda = aMonoLambdaInRegion("foo")
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
    public void monoLambdaAutoregistersUseCasesInInjectMaid() {
        final MonoLambda monoLambda = aMonoLambdaInRegion("foo")
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
    public void monoLambdaCanHaveCustomUseCaseRegistrations() {
        final MonoLambda monoLambda = aMonoLambdaInRegion("foo")
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
    public void monoLambdaCanRegisterCognitoAuthorizer() {
        aMonoLambdaInRegion("foo")
                .withCognitoAuthorization(
                        "a",
                        "b",
                        request -> "c",
                        (request, getUserResponse, authorizationToken) -> Map.of()
                )
                .build();
    }

    @Test
    public void monoLambdaCanHandleHttpRequest() {
        final MonoLambda monoLambda = aMonoLambdaInRegion("foo")
                .withHttpMaid(httpMaidBuilder -> httpMaidBuilder
                        .get("/", (request, response) -> response.setBody("abc"))
                )
                .build();

        final Map<String, Object> response = monoLambda.handleRequest(Map.of(
                "requestContext", Map.of(),
                "httpMethod", "GET",
                "path", "/"
        ));

        assertThat(response, is(Map.of(
                "statusCode", OK,
                "multiValueHeaders", Map.of(),
                "body", "abc"
        )));
    }

    @Test
    public void monoLambdaCanHandleAuthRequest() {
        final MonoLambda monoLambda = aMonoLambdaInRegion("foo")
                .withHttpMaid(httpMaidBuilder -> httpMaidBuilder
                        .get("/", (request, response) -> response.setBody("abc"))
                )
                .build();

        final Map<String, Object> response = monoLambda.handleRequest(Map.of(
                "requestContext", Map.of("connectionId", "foo"),
                "type", "REQUEST",
                "methodArn", "xyz"
        ));
        final Object policyDocument = response.get("policyDocument");

        assertThat(policyDocument, is(Map.of(
                "Statement", List.of(Map.of(
                        "Effect", "Allow",
                        "Resource", "xyz",
                        "Action", "execute-api:Invoke"
                )),
                "Version", "2012-10-17"
                )
        ));
    }

    @Test
    public void monoLambdaCanHandleWebsocketRequest() {
        final MonoLambda monoLambda = aMonoLambdaInRegion("foo")
                .withHttpMaid(httpMaidBuilder -> httpMaidBuilder
                        .get("/", (request, response) -> response.setBody("abc"))
                )
                .build();

        final Map<String, Object> response = monoLambda.handleRequest(Map.of(
                "requestContext", Map.of(
                        "connectionId", "foo",
                        "eventType", "CONNECT",
                        "stage", "a",
                        "apiId", "b"
                ),
                "type", "CONNECT"
        ));

        assertThat(response, is(Map.of()));
    }
}
