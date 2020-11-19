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
import de.quantummaid.httpmaid.awslambda.AwsLambdaEndpoint;
import de.quantummaid.httpmaid.awslambda.AwsWebsocketLambdaEndpoint;
import de.quantummaid.httpmaid.awslambdacognitoauthorizer.CognitoLambdaAuthorizer;
import de.quantummaid.httpmaid.awslambdacognitoauthorizer.TokenExtractor;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static de.quantummaid.httpmaid.awslambda.AwsLambdaEndpoint.awsLambdaEndpointFor;
import static de.quantummaid.httpmaid.awslambda.AwsWebsocketLambdaEndpoint.awsWebsocketLambdaEndpointFor;
import static de.quantummaid.httpmaid.awslambda.EventUtils.isAuthorizationRequest;
import static de.quantummaid.httpmaid.awslambda.EventUtils.isWebSocketRequest;
import static de.quantummaid.httpmaid.awslambdacognitoauthorizer.CognitoLambdaAuthorizer.cognitoLambdaAuthorizer;
import static de.quantummaid.quantummaid.integrations.monolambda.MonoLambdaBuilder.monoLambdaBuilder;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonoLambda {
    private final HttpMaid httpMaid;
    private final AwsLambdaEndpoint httpEndpoint;
    private final AwsWebsocketLambdaEndpoint websocketEndpoint;
    private final CognitoLambdaAuthorizer authorizer;

    public static MonoLambdaBuilder aMonoLambda() {
        return monoLambdaBuilder();
    }

    static MonoLambda fromHttpMaid(final HttpMaid httpMaid, final TokenExtractor tokenExtractor) {
        final AwsLambdaEndpoint httpEndpoint = awsLambdaEndpointFor(httpMaid);
        final AwsWebsocketLambdaEndpoint websocketEndpoint = awsWebsocketLambdaEndpointFor(httpMaid);
        final CognitoLambdaAuthorizer authorizer = cognitoLambdaAuthorizer(tokenExtractor);
        return new MonoLambda(httpMaid, httpEndpoint, websocketEndpoint, authorizer);
    }

    public Map<String, Object> handleRequest(final Map<String, Object> event) {
        log.info("lambda request: {}", event);
        if (isAuthorizationRequest(event)) {
            log.info("request has been classified as an authorization request");
            return authorizer.delegate(event);
        } else if (!isWebSocketRequest(event)) {
            log.info("request has been classified as a plain HTTP request");
            return httpEndpoint.delegate(event);
        } else {
            log.info("request has been classified as a websocket request");
            return websocketEndpoint.delegate(event);
        }
    }

    public HttpMaid httpMaid() {
        return httpMaid;
    }
}
