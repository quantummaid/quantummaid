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

import de.quantummaid.httpmaid.HttpConfiguration;
import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.httpmaid.HttpMaidBuilder;
import de.quantummaid.httpmaid.PerRouteConfigurator;
import de.quantummaid.httpmaid.chains.Configurator;
import de.quantummaid.httpmaid.generator.builder.ConditionStage;
import de.quantummaid.httpmaid.usecases.UseCasesModule;
import de.quantummaid.reflectmaid.ReflectMaid;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static de.quantummaid.httpmaid.HttpMaid.STARTUP_TIME;
import static de.quantummaid.httpmaid.HttpMaid.anHttpMaid;
import static de.quantummaid.httpmaid.chains.Configurator.toUseModules;
import static de.quantummaid.httpmaid.usecases.UseCasesModule.useCasesModule;
import static de.quantummaid.quantummaid.EndpointCreator.pureJavaEndpointCreator;
import static de.quantummaid.quantummaid.UniqueAccessManager.claim;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.time.Duration.between;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class QuantumMaid implements HttpConfiguration<QuantumMaid>, AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuantumMaid.class);

    private final HttpMaidBuilder httpMaidBuilder;
    private HttpMaid httpMaid;
    private final List<EndpointCreator> endpoints = new ArrayList<>(1);
    private final List<String> endpointUrls = new ArrayList<>(1);
    private final CountDownLatch terminationTrigger = new CountDownLatch(1);
    private final CountDownLatch termination = new CountDownLatch(1);
    private final Thread shutdownHook = new Thread(this::close);

    public static QuantumMaid quantumMaid() {
        final ReflectMaid reflectMaid = ReflectMaid.aReflectMaid();
        return quantumMaid(reflectMaid);
    }

    public static QuantumMaid quantumMaid(final ReflectMaid reflectMaid) {
        final UseCasesModule useCasesModule = useCasesModule();
        final HttpMaidBuilder httpMaidBuilder = anHttpMaid(reflectMaid)
                .configured(toUseModules(useCasesModule))
                .disableAutodectectionOfModules();
        return new QuantumMaid(httpMaidBuilder);
    }

    @Override
    public ConditionStage<QuantumMaid> serving(final Object handler,
                                               final PerRouteConfigurator... perRouteConfigurators) {
        return condition -> {
            httpMaidBuilder.serving(handler, perRouteConfigurators).when(condition);
            return this;
        };
    }

    @Override
    public QuantumMaid configured(final Configurator configurator) {
        httpMaidBuilder.configured(configurator);
        return this;
    }

    /**
     * @deprecated Will be removed soon. Configure HTTP aspects directly on QuantumMaid instance instead.
     */
    @SuppressWarnings("java:S1133")
    @Deprecated(since = "1.0.51", forRemoval = true)
    public QuantumMaid withHttpMaid(final HttpMaid httpMaid) {
        claim(httpMaid);
        this.httpMaid = httpMaid;
        return this;
    }

    public QuantumMaid withLocalHostEndpointOnPort(final int port) {
        this.endpoints.add(pureJavaEndpointCreator(port));
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
        try (HttpMaid managedHttpMaid = httpMaid()) {
            final Instant begin = Instant.now();
            this.endpoints.forEach(endpoint -> endpoint.createEndpoint(managedHttpMaid));
            final Instant end = Instant.now();
            final Duration endpointStartUpTime = between(begin, end);
            renderSplash(endpointStartUpTime);
            getRuntime().addShutdownHook(shutdownHook);
            initialization.countDown();
            awaitCountDownLatch(terminationTrigger);
        }
        termination.countDown();
    }

    public HttpMaid httpMaid() {
        if (httpMaid == null) {
            httpMaid = httpMaidBuilder.build();
        }
        return httpMaid;
    }

    @Override
    public void close() {
        terminationTrigger.countDown();
        awaitCountDownLatch(terminationTrigger);
        if (!currentThread().equals(shutdownHook)) {
            getRuntime().removeShutdownHook(shutdownHook);
        }
    }

    private void renderSplash(final Duration endpointStartupTime) {
        LOGGER.info("\n" + Logo.LOGO_TEXT + "\n");
        final Duration httpMaidStartUpTime = httpMaid.getMetaDatum(STARTUP_TIME);
        final long httpMaidMilliseconds = TimeUnit.MILLISECONDS.convert(httpMaidStartUpTime);
        final long endpointsMilliseconds = TimeUnit.MILLISECONDS.convert(endpointStartupTime);
        final long combinedMilliseconds = httpMaidMilliseconds + endpointsMilliseconds;
        LOGGER.info("Startup took: {}ms ({}ms initialization, {}ms endpoint startup)",
                combinedMilliseconds, httpMaidMilliseconds, endpointsMilliseconds);
        endpointUrls.forEach(url -> LOGGER.info(format("Serving %s", url)));
        LOGGER.info("Ready.");
    }

    private static void awaitCountDownLatch(final CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (final InterruptedException e) {
            currentThread().interrupt();
        }
    }
}
