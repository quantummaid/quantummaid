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
import de.quantummaid.quantummaid.integrations.testsupport.FreePortPool;
import de.quantummaid.reflectmaid.ReflectMaid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static de.quantummaid.httpmaid.client.HttpMaidClient.aHttpMaidClientForTheHost;
import static de.quantummaid.httpmaid.jetty.JettyWebsocketEndpoint.jettyWebsocketEndpoint;
import static de.quantummaid.quantummaid.integrations.testmonolambda.TestMonoLambdaBuilder.testMonoLambdaBuilder;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestMonoLambda implements AutoCloseable {
    private final HttpMaid httpMaid;
    private final int port;

    public static TestMonoLambdaBuilder aTestMonoLambda() {
        final int port = FreePortPool.freePort();
        return aTestMonoLambda(port);
    }

    public static TestMonoLambdaBuilder aTestMonoLambda(final int port) {
        final ReflectMaid reflectMaid = ReflectMaid.aReflectMaid();
        return aTestMonoLambda(reflectMaid, port);
    }

    public static TestMonoLambdaBuilder aTestMonoLambda(final ReflectMaid reflectMaid,
                                                        final int port) {
        return testMonoLambdaBuilder(reflectMaid, port);
    }

    static TestMonoLambda fromHttpMaid(final HttpMaid httpMaid, final int port) {
        jettyWebsocketEndpoint(httpMaid, port);
        return new TestMonoLambda(httpMaid, port);
    }

    public HttpMaid httpMaid() {
        return httpMaid;
    }

    public int port() {
        return port;
    }

    public HttpMaidClient connectClient() {
        return aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
    }

    @Override
    public void close() {
        httpMaid.close();
    }
}
