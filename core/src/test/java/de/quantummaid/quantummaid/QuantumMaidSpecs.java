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

import org.junit.jupiter.api.Test;

import static de.quantummaid.httpmaid.HttpMaid.anHttpMaid;
import static de.quantummaid.quantummaid.PortUtils.assertPortIsClosed;
import static de.quantummaid.quantummaid.PortUtils.waitForPortToBeAvailable;
import static de.quantummaid.quantummaid.QuantumMaid.quantumMaid;

public final class QuantumMaidSpecs {

    @Test
    public void quantumMaidClosesHttpMaidOnExit() {
        final int port = 1337;
        final QuantumMaid quantumMaid = quantumMaid()
                .withHttpMaid(
                        anHttpMaid()
                                .get("/", (request, response) -> response.setBody("I'm up"))
                                .build()
                )
                .withLocalHostEndpointOnPort(port);
        quantumMaid.runAsynchronously();
        waitForPortToBeAvailable(port);
        quantumMaid.close();
        PortUtils.waitForPortToClose(port);
        assertPortIsClosed(port);
    }
}