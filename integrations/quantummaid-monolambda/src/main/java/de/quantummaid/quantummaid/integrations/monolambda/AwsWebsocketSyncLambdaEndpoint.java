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
import de.quantummaid.httpmaid.awslambda.AwsLambdaEvent;
import de.quantummaid.httpmaid.awslambda.AwsWebsocketConnectionInformation;
import de.quantummaid.httpmaid.awslambda.AwsWebsocketSender;
import de.quantummaid.httpmaid.awslambda.apigateway.ApiGatewayClientFactory;
import de.quantummaid.httpmaid.chains.MetaDataKey;
import de.quantummaid.httpmaid.http.Headers;
import de.quantummaid.httpmaid.http.QueryParameters;
import de.quantummaid.httpmaid.websockets.authorization.AuthorizationDecision;
import de.quantummaid.httpmaid.websockets.endpoint.RawWebsocketConnectBuilder;
import de.quantummaid.httpmaid.websockets.endpoint.RawWebsocketMessage;
import de.quantummaid.httpmaid.websockets.registry.ConnectionInformation;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.quantummaid.httpmaid.awslambda.AwsLambdaEvent.AWS_LAMBDA_EVENT;
import static de.quantummaid.httpmaid.awslambda.AwsLambdaEvent.awsLambdaEvent;
import static de.quantummaid.httpmaid.awslambda.AwsWebsocketAuthorizationException.awsWebsocketAuthorizationException;
import static de.quantummaid.httpmaid.awslambda.AwsWebsocketConnectionInformation.awsWebsocketConnectionInformation;
import static de.quantummaid.httpmaid.awslambda.AwsWebsocketSender.AWS_WEBSOCKET_SENDER;
import static de.quantummaid.httpmaid.awslambda.MapDeserializer.mapFromString;
import static de.quantummaid.httpmaid.awslambda.WebsocketEventUtils.extractHeaders;
import static de.quantummaid.httpmaid.awslambda.WebsocketEventUtils.extractQueryParameters;
import static de.quantummaid.httpmaid.awslambda.apigateway.DefaultApiGatewayClientFactory.defaultApiGatewayClientFactory;
import static de.quantummaid.httpmaid.awslambda.authorizer.LambdaWebsocketAuthorizer.*;
import static de.quantummaid.httpmaid.util.Validators.validateNotNull;
import static de.quantummaid.httpmaid.util.Validators.validateNotNullNorEmpty;
import static de.quantummaid.httpmaid.websockets.WebsocketMetaDataKeys.ADDITIONAL_WEBSOCKET_DATA;
import static de.quantummaid.httpmaid.websockets.endpoint.RawWebsocketConnectBuilder.rawWebsocketConnectBuilder;
import static de.quantummaid.httpmaid.websockets.endpoint.RawWebsocketDisconnect.rawWebsocketDisconnect;
import static de.quantummaid.httpmaid.websockets.endpoint.RawWebsocketMessage.rawWebsocketMessageWithMetaData;
import static de.quantummaid.quantummaid.integrations.monolambda.AwsWebsocketSyncSender.awsWebsocketSyncSender;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AwsWebsocketSyncLambdaEndpoint {
    private static final String CONNECT_EVENT_TYPE = "CONNECT";
    private static final String DISCONNECT_EVENT_TYPE = "DISCONNECT";
    private static final String MESSAGE_EVENT_TYPE = "MESSAGE";
    private static final String REQUEST_CONTEXT_KEY = "requestContext";
    private static final String AUTHORIZER_KEY = "authorizer";

    private final HttpMaid httpMaid;
    private final String region;

    public static AwsWebsocketSyncLambdaEndpoint awsWebsocketSyncLambdaEndpointFor(
            final HttpMaid httpMaid,
            final String region,
            final ApiGatewaySyncClientFactory apiGatewayClientFactory) {
        validateNotNull(httpMaid, "httpMaid");
        validateNotNullNorEmpty(region, "region");
        validateNotNull(apiGatewayClientFactory, "apiGatewayClientFactory");
        final AwsWebsocketSyncSender websocketSender = awsWebsocketSyncSender(apiGatewayClientFactory);
        httpMaid.addWebsocketSender(AWS_WEBSOCKET_SENDER, websocketSender);
        return new AwsWebsocketSyncLambdaEndpoint(httpMaid, region);
    }

    private static Map<String, Object> extractAdditionalData(final AwsLambdaEvent authorizerContext) {
        final String serializedAdditionalWebsocketData = authorizerContext
                .getAsString(ADDITIONAL_DATA_KEY);
        return mapFromString(serializedAdditionalWebsocketData);
    }

    public Map<String, Object> delegate(final Map<String, Object> event) {
        final AwsLambdaEvent awsLambdaEvent = awsLambdaEvent(event);
        return handleWebsocketRequest(awsLambdaEvent);
    }

    private Map<String, Object> handleWebsocketRequest(final AwsLambdaEvent event) {
        final AwsLambdaEvent requestContext = event.getMap(REQUEST_CONTEXT_KEY);
        final String eventType = requestContext.getAsString("eventType");
        final String connectionId = requestContext.getAsString("connectionId");
        final String stage = requestContext.getAsString("stage");
        final String apiId = requestContext.getAsString("apiId");
        final AwsWebsocketConnectionInformation connectionInformation = awsWebsocketConnectionInformation(connectionId, stage, apiId, region);
        if (CONNECT_EVENT_TYPE.equals(eventType)) {
            handleConnect(event, connectionInformation);
            return emptyMap();
        } else if (DISCONNECT_EVENT_TYPE.equals(eventType)) {
            handleDisconnect(connectionInformation, event);
            return emptyMap();
        } else if (MESSAGE_EVENT_TYPE.equals(eventType)) {
            return handleMessage(event, connectionInformation);
        } else {
            throw new UnsupportedOperationException(format("Unsupported lambda event type '%s' with event '%s'", eventType, event));
        }
    }

    private void handleConnect(final AwsLambdaEvent event,
                               final AwsWebsocketConnectionInformation connectionInformation) {
        final Map<String, Object> additionalWebsocketData;
        if (isAlreadyAuthorized(event)) {
            additionalWebsocketData = extractAdditionalData(event
                    .getMap(REQUEST_CONTEXT_KEY)
                    .getMap(AUTHORIZER_KEY));
        } else {
            final AuthorizationDecision authorizationDecision = authorize(event, httpMaid);
            if (!authorizationDecision.isAuthorized()) {
                throw awsWebsocketAuthorizationException();
            }
            additionalWebsocketData = authorizationDecision.additionalData();
        }
        httpMaid.handleRequest(() -> {
            final RawWebsocketConnectBuilder builder = rawWebsocketConnectBuilder();
            builder.withConnectionInformation(AWS_WEBSOCKET_SENDER, connectionInformation);
            builder.withAdditionalMetaData(AWS_LAMBDA_EVENT, event);
            final QueryParameters queryParameters = extractQueryParameters(event);
            builder.withQueryParameters(queryParameters);
            final Headers headers = extractHeaders(event);
            builder.withHeaders(headers);
            builder.withAdditionalMetaData(ADDITIONAL_WEBSOCKET_DATA, additionalWebsocketData);
            return builder.build();
        }, ignored -> {
        });
    }

    private void handleDisconnect(final AwsWebsocketConnectionInformation connectionInformation,
                                  final AwsLambdaEvent event) {
        httpMaid.handleRequest(() -> rawWebsocketDisconnect(connectionInformation,
                Map.of(AWS_LAMBDA_EVENT, event)), response -> {
        });
    }

    private Map<String, Object> handleMessage(final AwsLambdaEvent event,
                                              final ConnectionInformation connectionInformation) {
        return httpMaid.handleRequestSynchronously(() -> {
            final String body = event.getAsString("body");
            final Map<MetaDataKey<?>, Object> additionalMetaData = Map.of(AWS_LAMBDA_EVENT, event);
            if (isAlreadyAuthorized(event)) {
                final AwsLambdaEvent authorizerContext = event
                        .getMap(REQUEST_CONTEXT_KEY)
                        .getMap(AUTHORIZER_KEY);
                final String serializedEvent = authorizerContext
                        .getAsString(AUTHORIZER_EVENT_KEY);
                final Map<String, Object> authorizerEventMap = mapFromString(serializedEvent);
                final AwsLambdaEvent authorizerEvent = awsLambdaEvent(authorizerEventMap);
                final QueryParameters queryParameters = extractQueryParameters(authorizerEvent);
                final Headers headers = extractHeaders(authorizerEvent);
                final Map<String, Object> additionalData = extractAdditionalData(authorizerContext);
                return rawWebsocketMessageWithMetaData(
                        connectionInformation,
                        body,
                        queryParameters,
                        headers,
                        additionalData,
                        additionalMetaData
                );
            } else {
                return RawWebsocketMessage.rawWebsocketMessage(connectionInformation,
                        body,
                        additionalMetaData
                );
            }
        }, response -> {
            final LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
            response.optionalStringBody().ifPresent(s -> responseMap.put("body", s));
            return responseMap;
        });
    }

    private boolean isAlreadyAuthorized(final AwsLambdaEvent event) {
        final AwsLambdaEvent context = event.getMap(REQUEST_CONTEXT_KEY);
        return context.containsKey(AUTHORIZER_KEY);
    }
}
