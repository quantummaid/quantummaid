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

import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.httpmaid.client.HttpMaidClient;
import de.quantummaid.quantummaid.QuantumMaid;
import de.quantummaid.quantummaid.integrations.testsupport.QuantumMaidProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.atomic.AtomicBoolean;

import static de.quantummaid.httpmaid.client.HttpClientRequest.aGetRequestToThePath;
import static de.quantummaid.httpmaid.client.HttpMaidClient.aHttpMaidClientForTheHost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Execution(ExecutionMode.CONCURRENT)
@QuantumMaidTest
public final class TestsCanBeRunInParallelSpecs implements QuantumMaidProvider {

    @Override
    public QuantumMaid provide(final int port) {
        final AtomicBoolean hasAlreadyBeenCalled = new AtomicBoolean(false);
        final HttpMaid httpMaid = HttpMaid.anHttpMaid()
                .get("/", (request, response) -> {
                    final boolean alreadySet = hasAlreadyBeenCalled.getAndSet(true);
                    if(!alreadySet) {
                        response.setBody("first attempt");
                    } else {
                        response.setBody("not first attempt");
                    }
                })
                .build();
        return QuantumMaid.quantumMaid()
                .withHttpMaid(httpMaid)
                .withLocalHostEndpointOnPort(port);
    }

    @Test
    public void test000(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test001(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test002(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test003(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test004(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test005(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test006(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test007(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test008(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test009(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test010(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test011(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test012(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test013(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test014(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test015(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test016(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test017(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test018(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test019(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test020(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test021(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test022(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test023(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test024(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test025(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test026(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test027(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test028(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test029(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test030(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test031(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test032(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test033(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test034(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test035(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test036(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test037(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test038(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test039(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test040(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test041(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test042(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test043(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test044(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test045(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test046(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test047(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test048(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test049(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test050(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test051(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test052(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test053(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test054(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test055(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test056(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test057(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test058(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test059(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test060(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test061(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test062(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test063(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test064(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test065(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test066(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test067(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test068(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test069(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test070(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test071(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test072(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test073(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test074(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test075(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test076(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test077(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test078(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test079(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test080(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test081(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test082(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test083(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test084(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test085(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test086(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test087(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test088(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test089(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test090(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test091(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test092(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test093(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test094(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test095(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test096(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test097(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test098(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }

    @Test
    public void test099(final int port) {
        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("first attempt"));
    }
}
