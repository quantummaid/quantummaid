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

package de.quantummaid.quantummaid.injectmaid;

import de.quantummaid.httpmaid.usecases.instantiation.UseCaseInstantiator;
import de.quantummaid.httpmaid.usecases.instantiation.UseCaseInstantiatorFactory;
import de.quantummaid.injectmaid.InjectMaid;
import de.quantummaid.injectmaid.InjectMaidBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static de.quantummaid.quantummaid.injectmaid.InjectMaidInstantiator.injectMaidInstantiator;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InjectMaidInstantiatorFactory implements UseCaseInstantiatorFactory {

    public static InjectMaidInstantiatorFactory injectMaidInstantiatorFactory() {
        return new InjectMaidInstantiatorFactory();
    }

    @Override
    public UseCaseInstantiator createInstantiator(final List<Class<?>> requiredTypes) {
        final InjectMaidBuilder builder = InjectMaid.anInjectMaid();
        requiredTypes.forEach(builder::withType);
        final InjectMaid injectMaid = builder.build();
        return injectMaidInstantiator(injectMaid);
    }
}
