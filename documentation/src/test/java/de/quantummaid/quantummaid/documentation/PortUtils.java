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
import java.net.Socket;
import java.util.Objects;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;

public final class PortUtils {

    private PortUtils() {
    }

    public static void assertPortIsClosed(final int port) {
        final boolean closed = !portIsOpen(port);
        assertThat(format("port '%d' is closed", port), closed);
    }

    public static void assertPortIsOpen(final int port) {
        final boolean open = portIsOpen(port);
        assertThat(format("port '%d' is open", port), open);
    }

    public static void waitForPortToBeAvailable(final int port) {
        final int maxTries = 100;
        for (int i = 0; i < maxTries; ++i) {
            if (portIsOpen(port)) {
                return;
            }
            System.out.println(format("Waiting for port %d (%d/%d)", port, i, maxTries));
            try {
                sleep(1_000);
            } catch (final InterruptedException e) {
                currentThread().interrupt();
            }
        }
        throw new RuntimeException(format("Port %d did not become available in time", port));
    }

    public static void waitForPortToClose(final int port) {
        final int maxTries = 100;
        for (int i = 0; i < maxTries; ++i) {
            if (!portIsOpen(port)) {
                return;
            }
            System.out.println(format("Waiting for port %d to close (%d/%d)", port, i, maxTries));
            try {
                sleep(1_000);
            } catch (final InterruptedException e) {
                currentThread().interrupt();
            }
        }
        throw new RuntimeException(format("Port %d did not close in time", port));
    }

    private static boolean portIsOpen(final int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            // dummy operation so warning disappears:
            Objects.requireNonNull(ignored);
            return true;
        } catch (final IOException ignored) {
            return false;
        }
    }
}
