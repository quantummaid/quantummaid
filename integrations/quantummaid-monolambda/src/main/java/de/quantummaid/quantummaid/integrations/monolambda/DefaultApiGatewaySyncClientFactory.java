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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClientBuilder;

import java.net.URI;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultApiGatewaySyncClientFactory implements ApiGatewaySyncClientFactory {
    private final AwsCredentialsProvider credentialsProvider;
    private final SdkHttpClient httpClient;

    public static ApiGatewaySyncClientFactory defaultApiGatewayClientFactory() {
        final AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
        final SdkHttpClient httpClient = ApacheHttpClient.builder().build();
        return new DefaultApiGatewaySyncClientFactory(credentialsProvider, httpClient);
    }

    @Override
    public ApiGatewayManagementApiClient provide(final String endpointUrl) {
        final ApiGatewayManagementApiClientBuilder apiGatewayManagementApiClientBuilder = ApiGatewayManagementApiClient.builder()
                .credentialsProvider(credentialsProvider)
                .httpClient(httpClient)
                .endpointOverride(URI.create(endpointUrl));
        return apiGatewayManagementApiClientBuilder
                .build();
    }
}
