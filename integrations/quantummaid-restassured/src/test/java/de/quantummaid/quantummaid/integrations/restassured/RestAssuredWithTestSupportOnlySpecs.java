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

package de.quantummaid.quantummaid.integrations.restassured;

import de.quantummaid.quantummaid.QuantumMaid;
import de.quantummaid.quantummaid.integrations.testsupport.QuantumMaidProvider;
import de.quantummaid.quantummaid.integrations.testsupport.TestSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;

public final class RestAssuredWithTestSupportOnlySpecs implements QuantumMaidProvider {
    private static final TestSupport TEST_SUPPORT = TestSupport.testSupport();

    @Override
    public QuantumMaid provide(final int port) {
        return QuantumMaid.quantumMaid()
                .get("/", (request, response) -> response.setBody("foo"))
                .withLocalHostEndpointOnPort(port);
    }

    @BeforeAll
    public static void setExtension() {
        TEST_SUPPORT.setExtensions(singletonList(new RestAssuredExtension()));
    }

    @BeforeEach
    public void init() {
        TEST_SUPPORT.init(this);
    }

    @AfterAll
    public static void cleanUp() {
        TEST_SUPPORT.close();
    }

    @Test
    public void restAssuredIsInitialized() {
        given()
                .when().get("/")
                .then()
                .body(is("foo"));
    }
}
