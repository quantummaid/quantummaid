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

package de.quantummaid.quantummaid.documentation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

final class FreePortPool {
    private static final int START_PORT = 9000;
    private static final int HIGHEST_PORT = 65535;
    private static final InetAddress LOCALHOST = localhost();
    private static int currentPort = START_PORT;

    private FreePortPool() {
    }

    static synchronized int freePort() {
        currentPort = currentPort + 1;
        if (currentPort >= HIGHEST_PORT) {
            currentPort = START_PORT;
            return freePort();
        } else {
            try {
                final ServerSocket serverSocket = new ServerSocket(currentPort, 0, LOCALHOST);
                serverSocket.close();
                return currentPort;
            } catch (final IOException ex) {
                return freePort();
            }
        }
    }

    private static InetAddress localhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (final UnknownHostException e) {
            throw new UnsupportedOperationException("This should never happen", e);
        }
    }
}
