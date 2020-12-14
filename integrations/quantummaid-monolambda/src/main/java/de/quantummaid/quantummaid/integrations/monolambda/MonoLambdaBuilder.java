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
import de.quantummaid.httpmaid.mapmaid.MapMaidConfigurators;
import de.quantummaid.httpmaid.mapmaid.MapMaidModule;
import de.quantummaid.httpmaid.usecases.UseCasesModule;
import de.quantummaid.httpmaid.websockets.additionaldata.AdditionalWebsocketDataProvider;
import de.quantummaid.httpmaid.websockets.authorization.WebsocketAuthorizer;
import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.injectmaid.InjectMaidBuilder;
import de.quantummaid.quantummaid.injectmaid.InjectMaidInstantiatorFactory;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static de.quantummaid.httpmaid.HttpMaid.anHttpMaid;
import static de.quantummaid.httpmaid.chains.Configurator.toUseModules;
import static de.quantummaid.httpmaid.mapmaid.MapMaidModule.mapMaidModule;
import static de.quantummaid.httpmaid.usecases.UseCasesModule.useCasesModule;
import static de.quantummaid.httpmaid.websockets.WebsocketConfigurators.toAuthorizeWebsocketsUsing;
import static de.quantummaid.httpmaid.websockets.WebsocketConfigurators.toStoreAdditionalDataInWebsocketContext;
import static de.quantummaid.mapmaid.minimaljson.MinimalJsonMarshallerAndUnmarshaller.minimalJsonMarshallerAndUnmarshaller;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.quantummaid.injectmaid.InjectMaidInstantiatorFactory.injectMaidInstantiatorFactory;
import static de.quantummaid.quantummaid.integrations.monolambda.MonoLambda.fromHttpMaid;

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

    public MonoLambda build() {
        final InjectMaidBuilder injectMaidBuilder = InjectMaid.anInjectMaid();
        injectorConfiguration.accept(injectMaidBuilder);

        final UseCasesModule useCasesModule = useCasesModule();
        final InjectMaidInstantiatorFactory instantiatorFactory =
                injectMaidInstantiatorFactory(injectMaidBuilder, useCaseRegistrationFilter);
        useCasesModule.setUseCaseInstantiatorFactory(instantiatorFactory);
        final MapMaidModule mapMaidModule = mapMaidModule();
        final HttpMaidBuilder httpMaidBuilder = anHttpMaid()
                .configured(toUseModules(useCasesModule, mapMaidModule))
                .configured(MapMaidConfigurators.toConfigureMapMaidUsingRecipe(mapMaidBuilder ->
                        mapMaidBuilder.withAdvancedSettings(advancedBuilder -> advancedBuilder
                                .doNotAutoloadMarshallers()
                                .usingMarshaller(minimalJsonMarshallerAndUnmarshaller()))))
                .disableAutodectectionOfModules();

        if (websocketAuthorizer != null) {
            httpMaidBuilder.configured(toAuthorizeWebsocketsUsing(websocketAuthorizer));
        }
        if (additionalWebsocketDataProvider != null) {
            httpMaidBuilder.configured(toStoreAdditionalDataInWebsocketContext(additionalWebsocketDataProvider));
        }

        httpConfiguration.accept(httpMaidBuilder);

        final long startTime = System.currentTimeMillis();
        final HttpMaid httpMaid = httpMaidBuilder.build();
        final long endTime = System.currentTimeMillis();
        final long time = endTime - startTime;
        if (log.isInfoEnabled()) {
            log.info("construction of HttpMaid took {}ms", time);
        }

        return fromHttpMaid(httpMaid, region);
    }
}
