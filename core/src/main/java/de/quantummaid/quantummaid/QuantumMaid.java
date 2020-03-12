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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static de.quantummaid.httpmaid.HttpMaid.STARTUP_TIME;
import static de.quantummaid.httpmaid.purejavaendpoint.PureJavaEndpoint.pureJavaEndpointFor;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;

public final class QuantumMaid {
    private static final int SLEEP_TIME = 10_000;

    private volatile HttpMaid httpMaid;
    private final List<Consumer<HttpMaid>> endpoints = new ArrayList<>(1);

    public static QuantumMaid quantumMaid() {
        return new QuantumMaid();
    }

    public QuantumMaid withHttpMaid(final HttpMaid httpMaid) {
        this.httpMaid = httpMaid;
        return this;
    }

    public QuantumMaid withLocalHostEndpointOnPort(final int port) {
        this.endpoints.add(httpMaid -> pureJavaEndpointFor(httpMaid).listeningOnThePort(port));
        return this;
    }

    public void runAsynchronously() {
        this.endpoints.forEach(endpoint -> endpoint.accept(httpMaid));
    }

    public void run() {
        renderSplash();
        try (HttpMaid httpMaid = this.httpMaid) {
            this.endpoints.forEach(endpoint -> endpoint.accept(httpMaid));
            while (true) {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (final InterruptedException e) {
                    currentThread().interrupt();
                }
            }
        }
    }

    public void close() {
        httpMaid.close();
    }

    private void renderSplash() {
        System.out.println(Logo.LOGO);
        final Duration startUpTime = httpMaid.getMetaDatum(STARTUP_TIME);
        final long nanoseconds = startUpTime.get(ChronoUnit.NANOS);
        System.out.println(format("Startup took: %sns", nanoseconds));
    }
}
