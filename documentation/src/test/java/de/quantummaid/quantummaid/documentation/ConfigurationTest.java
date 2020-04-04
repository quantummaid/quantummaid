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

package de.quantummaid.quantummaid.documentation;

import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.httpmaid.client.HttpMaidClient;
import de.quantummaid.quantummaid.QuantumMaid;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.quantummaid.httpmaid.client.HttpClientRequest.aGetRequestToThePath;
import static de.quantummaid.httpmaid.client.HttpMaidClient.aHttpMaidClientForTheHost;
import static de.quantummaid.quantummaid.documentation.FreePortPool.freePort;
import static de.quantummaid.quantummaid.documentation.PortUtils.waitForPortToBeAvailable;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ConfigurationTest {

    @Test
    public void configuration() {
        final int port = freePort();
        //Showcase start configuration
        final HttpMaid httpMaid = HttpMaid.anHttpMaid()
                .get("/", (request, response) -> response.setBody("Hello World!"))
                .build();
        final QuantumMaid quantumMaid = QuantumMaid.quantumMaid()
                .withHttpMaid(httpMaid)
                .withLocalHostEndpointOnPort(port);
        //Showcase end configuration
        //Showcase start runAsynchronously
        quantumMaid.runAsynchronously();
        //Showcase end runAsynchronously

        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("Hello World!"));

        //Showcase start close
        quantumMaid.close();
        //Showcase end close

        boolean success = false;
        try {
            client.issue(aGetRequestToThePath("/").mappedToString());
            success = true;
        } catch (final RuntimeException e) {
            assertThat(e.getCause(), instanceOf(IOException.class));
        }
        assertThat(success, is(false));
    }

    @Test
    public void runSynchronously() {
        final int port = freePort();
        final HttpMaid httpMaid = HttpMaid.anHttpMaid()
                .get("/", (request, response) -> response.setBody("Hello World!"))
                .build();
        final QuantumMaid quantumMaid = QuantumMaid.quantumMaid()
                .withHttpMaid(httpMaid)
                .withLocalHostEndpointOnPort(port);

        final Thread thread = new Thread(() -> {
            //Showcase start runSynchronously
            quantumMaid.run();
            //Showcase end runSynchronously
        });
        thread.start();
        waitForPortToBeAvailable(port);

        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(port)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("Hello World!"));
        thread.interrupt();
    }

    @Test
    public void tryWithResources() {
        final int port = freePort();
        final HttpMaid httpMaid = HttpMaid.anHttpMaid()
                .get("/", (request, response) -> response.setBody("Hello World!"))
                .build();
        //Showcase start tryWithResources
        try (QuantumMaid quantumMaid = QuantumMaid.quantumMaid()
                .withHttpMaid(httpMaid)
                .withLocalHostEndpointOnPort(port)) {
            quantumMaid.runAsynchronously();
            // do something
        }
        //Showcase end tryWithResources
    }
}
