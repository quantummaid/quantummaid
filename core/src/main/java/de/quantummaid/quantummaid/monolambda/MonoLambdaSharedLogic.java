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

package de.quantummaid.quantummaid.monolambda;

import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.httpmaid.HttpMaidBuilder;
import de.quantummaid.httpmaid.mapmaid.MapMaidModule;
import de.quantummaid.httpmaid.usecases.UseCasesModule;
import de.quantummaid.httpmaid.websockets.additionaldata.AdditionalWebsocketDataProvider;
import de.quantummaid.httpmaid.websockets.authorization.WebsocketAuthorizer;
import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.injectmaid.InjectMaidBuilder;
import de.quantummaid.mapmaid.builder.MarshallerAndUnmarshaller;
import de.quantummaid.quantummaid.injectmaid.InjectMaidInstantiatorFactory;
import de.quantummaid.reflectmaid.ReflectMaid;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static de.quantummaid.httpmaid.HttpMaid.anHttpMaid;
import static de.quantummaid.httpmaid.chains.Configurator.toUseModules;
import static de.quantummaid.httpmaid.mapmaid.MapMaidConfigurators.toConfigureMapMaidUsingRecipe;
import static de.quantummaid.httpmaid.mapmaid.MapMaidModule.mapMaidModule;
import static de.quantummaid.httpmaid.usecases.UseCasesModule.useCasesModule;
import static de.quantummaid.httpmaid.websockets.WebsocketConfigurators.toAuthorizeWebsocketsUsing;
import static de.quantummaid.httpmaid.websockets.WebsocketConfigurators.toStoreAdditionalDataInWebsocketContext;
import static de.quantummaid.quantummaid.injectmaid.InjectMaidInstantiatorFactory.injectMaidInstantiatorFactory;

@Slf4j
public final class MonoLambdaSharedLogic {

    private MonoLambdaSharedLogic() {
    }

    public static HttpMaid buildHttpMaid(final ReflectMaid reflectMaid,
                                         final Consumer<HttpMaidBuilder> httpConfiguration,
                                         final Consumer<InjectMaidBuilder> injectorConfiguration,
                                         final Predicate<Class<?>> useCaseRegistrationFilter,
                                         final MarshallerAndUnmarshaller<String> marshallerAndUnmarshaller,
                                         final WebsocketAuthorizer websocketAuthorizer,
                                         final AdditionalWebsocketDataProvider additionalWebsocketDataProvider) {
        final InjectMaidBuilder injectMaidBuilder = InjectMaid.anInjectMaid(reflectMaid);
        injectorConfiguration.accept(injectMaidBuilder);
        final UseCasesModule useCasesModule = useCasesModule();

        final InjectMaidInstantiatorFactory instantiatorFactory =
                injectMaidInstantiatorFactory(injectMaidBuilder, useCaseRegistrationFilter);

        useCasesModule.setUseCaseInstantiatorFactory(instantiatorFactory);

        final MapMaidModule mapMaidModule = mapMaidModule();

        final HttpMaidBuilder httpMaidBuilder = anHttpMaid(reflectMaid)
                .configured(toUseModules(useCasesModule, mapMaidModule))
                .configured(toConfigureMapMaidUsingRecipe(mapMaidBuilder ->
                        mapMaidBuilder.withAdvancedSettings(advancedBuilder -> advancedBuilder
                                .doNotAutoloadMarshallers()
                                .usingMarshaller(marshallerAndUnmarshaller))))
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
        return httpMaid;
    }
}
