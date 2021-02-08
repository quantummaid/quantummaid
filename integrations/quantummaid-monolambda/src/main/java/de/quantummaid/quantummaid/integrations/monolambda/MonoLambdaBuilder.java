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
import de.quantummaid.httpmaid.HttpMaidBuilder;
import de.quantummaid.httpmaid.awslambdacognitoauthorizer.CognitoContextEnricher;
import de.quantummaid.httpmaid.awslambdacognitoauthorizer.CognitoWebsocketAuthorizer;
import de.quantummaid.httpmaid.awslambdacognitoauthorizer.TokenExtractor;
import de.quantummaid.httpmaid.websockets.additionaldata.AdditionalWebsocketDataProvider;
import de.quantummaid.httpmaid.websockets.authorization.WebsocketAuthorizer;
import de.quantummaid.injectmaid.InjectMaidBuilder;
import de.quantummaid.mapmaid.minimaljson.MinimalJsonMarshallerAndUnmarshaller;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static de.quantummaid.mapmaid.minimaljson.MinimalJsonMarshallerAndUnmarshaller.minimalJsonMarshallerAndUnmarshaller;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.quantummaid.integrations.monolambda.DefaultApiGatewaySyncClientFactory.defaultApiGatewayClientFactory;
import static de.quantummaid.quantummaid.integrations.monolambda.MonoLambda.fromHttpMaid;
import static de.quantummaid.quantummaid.monolambda.MonoLambdaSharedLogic.buildHttpMaid;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class MonoLambdaBuilder {
    private final String region;
    private Consumer<HttpMaidBuilder> httpConfiguration = httpMaidBuilder -> {
    };
    private Consumer<InjectMaidBuilder> injectorConfiguration = injectMaidBuilder -> {
    };
    private Predicate<Class<?>> useCaseRegistrationFilter = useCase -> false;
    private WebsocketAuthorizer websocketAuthorizer;
    private AdditionalWebsocketDataProvider additionalWebsocketDataProvider;
    private ApiGatewaySyncClientFactory apiGatewayClientFactory;

    public static MonoLambdaBuilder monoLambdaBuilder() {
        final String region = System.getenv("AWS_REGION");
        return monoLambdaBuilder(region);
    }

    public static MonoLambdaBuilder monoLambdaBuilder(final String region) {
        validateNotNull(region, "region");
        return new MonoLambdaBuilder(region);
    }

    public MonoLambdaBuilder withHttpMaid(final Consumer<HttpMaidBuilder> httpConfiguration) {
        this.httpConfiguration = httpConfiguration;
        return this;
    }

    public MonoLambdaBuilder withInjectMaid(final Consumer<InjectMaidBuilder> injectorConfiguration) {
        this.injectorConfiguration = injectorConfiguration;
        return this;
    }

    public MonoLambdaBuilder skipAutomaticRegistrationOfUseCases() {
        return skipAutomaticRegistrationOfUseCasesThat(useCase -> true);
    }

    public MonoLambdaBuilder skipAutomaticRegistrationOfUseCasesThat(final Predicate<Class<?>> filter) {
        this.useCaseRegistrationFilter = filter;
        return this;
    }

    public MonoLambdaBuilder withWebsocketAuthorizer(final WebsocketAuthorizer authorizer) {
        validateNotNull(authorizer, "authorizer");
        this.websocketAuthorizer = authorizer;
        return this;
    }

    public MonoLambdaBuilder withAdditionalWebsocketDataProvider(
            final AdditionalWebsocketDataProvider additionalWebsocketDataProvider) {
        validateNotNull(additionalWebsocketDataProvider, "additionalWebsocketDataProvider");
        this.additionalWebsocketDataProvider = additionalWebsocketDataProvider;
        return this;
    }

    public MonoLambdaBuilder withCognitoAuthorization(final String userPoolId,
                                                      final String userPoolClientId,
                                                      final TokenExtractor tokenExtractor) {
        final CognitoIdentityProviderClient client = CognitoIdentityProviderClient.create();
        final String issuerUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
        final CognitoWebsocketAuthorizer authorizer = CognitoWebsocketAuthorizer.cognitoWebsocketAuthorizer(
                client,
                tokenExtractor,
                issuerUrl,
                userPoolClientId
        );
        return withWebsocketAuthorizer(authorizer);
    }

    public MonoLambdaBuilder withCognitoAuthorization(final String userPoolId,
                                                      final String userPoolClientId,
                                                      final TokenExtractor tokenExtractor,
                                                      final CognitoContextEnricher contextEnricher) {
        withCognitoAuthorization(userPoolId, userPoolClientId, tokenExtractor);
        return withAdditionalWebsocketDataProvider(contextEnricher);
    }

    public MonoLambdaBuilder withApiGatewayClientFactory(final ApiGatewaySyncClientFactory apiGatewayClientFactory) {
        this.apiGatewayClientFactory = apiGatewayClientFactory;
        return this;
    }

    public MonoLambda build() {
        final MinimalJsonMarshallerAndUnmarshaller marshallerAndUnmarshaller = minimalJsonMarshallerAndUnmarshaller();
        if (apiGatewayClientFactory == null) {
            apiGatewayClientFactory = defaultApiGatewayClientFactory();
        }
        final HttpMaid httpMaid = buildHttpMaid(
                httpConfiguration,
                injectorConfiguration,
                useCaseRegistrationFilter,
                marshallerAndUnmarshaller,
                websocketAuthorizer,
                additionalWebsocketDataProvider
        );
        return fromHttpMaid(httpMaid, region, apiGatewayClientFactory);
    }
}
