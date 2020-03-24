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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static de.quantummaid.httpmaid.HttpMaid.STARTUP_TIME;
import static de.quantummaid.httpmaid.purejavaendpoint.PureJavaEndpoint.pureJavaEndpointFor;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class QuantumMaid {
    private volatile HttpMaid httpMaid;
    private final List<Consumer<HttpMaid>> endpoints = new ArrayList<>(1);
    private final List<String> endpointUrls = new ArrayList<>(1);
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static QuantumMaid quantumMaid() {
        return new QuantumMaid();
    }

    public QuantumMaid withHttpMaid(final HttpMaid httpMaid) {
        this.httpMaid = httpMaid;
        return this;
    }

    public QuantumMaid withLocalHostEndpointOnPort(final int port) {
        this.endpoints.add(httpMaid -> pureJavaEndpointFor(httpMaid).listeningOnThePort(port));
        this.endpointUrls.add(format("http://localhost:%d/", port));
        return this;
    }

    public void runAsynchronously() {
        final Thread thread = new Thread(this::run);
        thread.start();
    }

    public void run() {
        renderSplash();
        try (HttpMaid httpMaid = this.httpMaid) {
            this.endpoints.forEach(endpoint -> endpoint.accept(httpMaid));
            try {
                countDownLatch.await();
            } catch (final InterruptedException e) {
                currentThread().interrupt();
            }
        }
    }

    public void close() {
        countDownLatch.countDown();
    }

    private void renderSplash() {
        System.out.println(Logo.LOGO + "\n");
        final Duration startUpTime = httpMaid.getMetaDatum(STARTUP_TIME);
        final long milliseconds = TimeUnit.MILLISECONDS.convert(startUpTime);
        System.out.println(format("Startup took: %sms", milliseconds));
        endpointUrls.forEach(url -> System.out.println(format("Serving %s", url)));
        System.out.println();
    }
}
