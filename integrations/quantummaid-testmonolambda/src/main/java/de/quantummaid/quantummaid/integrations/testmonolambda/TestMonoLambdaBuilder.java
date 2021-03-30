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
import de.quantummaid.httpmaid.HttpMaidBuilder;
import de.quantummaid.httpmaid.websockets.additionaldata.AdditionalWebsocketDataProvider;
import de.quantummaid.httpmaid.websockets.authorization.WebsocketAuthorizer;
import de.quantummaid.injectmaid.InjectMaidBuilder;
import de.quantummaid.mapmaid.minimaljson.MinimalJsonMarshallerAndUnmarshaller;
import de.quantummaid.reflectmaid.ReflectMaid;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static de.quantummaid.mapmaid.minimaljson.MinimalJsonMarshallerAndUnmarshaller.minimalJsonMarshallerAndUnmarshaller;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.quantummaid.integrations.testmonolambda.TestMonoLambda.fromHttpMaid;
import static de.quantummaid.quantummaid.monolambda.MonoLambdaSharedLogic.buildHttpMaid;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class TestMonoLambdaBuilder {
    private final ReflectMaid reflectMaid;
    private final int port;
    private Consumer<HttpMaidBuilder> httpConfiguration = httpMaidBuilder -> {
    };
    private Consumer<InjectMaidBuilder> injectorConfiguration = injectMaidBuilder -> {
    };
    private Predicate<Class<?>> useCaseRegistrationFilter = useCase -> false;
    private WebsocketAuthorizer websocketAuthorizer;
    private AdditionalWebsocketDataProvider additionalWebsocketDataProvider;

    public static TestMonoLambdaBuilder testMonoLambdaBuilder(final ReflectMaid reflectMaid,
                                                              final int port) {
        return new TestMonoLambdaBuilder(reflectMaid, port);
    }

    public TestMonoLambdaBuilder withHttpMaid(final Consumer<HttpMaidBuilder> httpConfiguration) {
        this.httpConfiguration = httpConfiguration;
        return this;
    }

    public TestMonoLambdaBuilder withInjectMaid(final Consumer<InjectMaidBuilder> injectorConfiguration) {
        this.injectorConfiguration = injectorConfiguration;
        return this;
    }

    public TestMonoLambdaBuilder skipAutomaticRegistrationOfUseCases() {
        return skipAutomaticRegistrationOfUseCasesThat(useCase -> true);
    }

    public TestMonoLambdaBuilder skipAutomaticRegistrationOfUseCasesThat(final Predicate<Class<?>> filter) {
        this.useCaseRegistrationFilter = filter;
        return this;
    }

    public TestMonoLambdaBuilder withWebsocketAuthorizer(final WebsocketAuthorizer authorizer) {
        validateNotNull(authorizer, "authorizer");
        this.websocketAuthorizer = authorizer;
        return this;
    }

    public TestMonoLambdaBuilder withAdditionalWebsocketDataProvider(
            final AdditionalWebsocketDataProvider additionalWebsocketDataProvider) {
        validateNotNull(additionalWebsocketDataProvider, "additionalWebsocketDataProvider");
        this.additionalWebsocketDataProvider = additionalWebsocketDataProvider;
        return this;
    }

    public TestMonoLambda build() {
        final MinimalJsonMarshallerAndUnmarshaller marshallerAndUnmarshaller = minimalJsonMarshallerAndUnmarshaller();
        final HttpMaid httpMaid = buildHttpMaid(
                reflectMaid,
                httpConfiguration,
                injectorConfiguration,
                useCaseRegistrationFilter,
                marshallerAndUnmarshaller,
                websocketAuthorizer,
                additionalWebsocketDataProvider
        );
        return fromHttpMaid(httpMaid, port);
    }
}
