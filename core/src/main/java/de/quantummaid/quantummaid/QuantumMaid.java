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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static de.quantummaid.httpmaid.HttpMaid.STARTUP_TIME;
import static de.quantummaid.httpmaid.purejavaendpoint.PureJavaEndpoint.pureJavaEndpointFor;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.time.Duration.between;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class QuantumMaid implements AutoCloseable {
    private volatile HttpMaid httpMaid;
    private final List<Consumer<HttpMaid>> endpoints = new ArrayList<>(1);
    private final List<String> endpointUrls = new ArrayList<>(1);
    private final CountDownLatch terminationTrigger = new CountDownLatch(1);
    private final CountDownLatch termination = new CountDownLatch(1);
    private final Thread shutdownHook = new Thread(this::close);

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
        final CountDownLatch initialization = new CountDownLatch(1);
        final Thread thread = new Thread(() -> run(initialization));
        thread.start();
        awaitCountDownLatch(initialization);
    }

    public void run() {
        run(new CountDownLatch(1));
    }

    private void run(final CountDownLatch initialization) {
        try (HttpMaid httpMaid = this.httpMaid) {
            final Instant begin = Instant.now();
            this.endpoints.forEach(endpoint -> endpoint.accept(httpMaid));
            final Instant end = Instant.now();
            final Duration endpointStartUpTime = between(begin, end);
            renderSplash(endpointStartUpTime);
            getRuntime().addShutdownHook(shutdownHook);
            initialization.countDown();
            awaitCountDownLatch(terminationTrigger);
        }
        termination.countDown();
    }

    @Override
    public void close() {
        terminationTrigger.countDown();
        awaitCountDownLatch(terminationTrigger);
        getRuntime().removeShutdownHook(shutdownHook);
    }

    private void renderSplash(final Duration endpointStartupTime) {
        System.out.println(Logo.LOGO + "\n");
        final Duration httpMaidStartUpTime = httpMaid.getMetaDatum(STARTUP_TIME);
        final long httpMaidMilliseconds = TimeUnit.MILLISECONDS.convert(httpMaidStartUpTime);
        final long endpointsMilliseconds = TimeUnit.MILLISECONDS.convert(endpointStartupTime);
        final long combinedMilliseconds = httpMaidMilliseconds + endpointsMilliseconds;
        System.out.println(format("Startup took: %sms (%sms initialization, %sms endpoint startup)",
                combinedMilliseconds, httpMaidMilliseconds, endpointsMilliseconds));
        endpointUrls.forEach(url -> System.out.println(format("Serving %s", url)));
        System.out.println();
    }

    private static void awaitCountDownLatch(final CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (final InterruptedException e) {
            currentThread().interrupt();
        }
    }
}
