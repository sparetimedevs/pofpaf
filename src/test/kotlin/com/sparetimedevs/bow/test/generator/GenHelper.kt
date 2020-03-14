/*
 * Copyright (c) 2020 sparetimedevs and respective authors and developers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sparetimedevs.bow.test.generator

import arrow.core.test.generators.fatalThrowable
import arrow.core.test.generators.throwable
import arrow.fx.IO
import com.microsoft.azure.functions.HttpMethod
import io.kotlintest.properties.Gen
import java.net.URI
import java.util.logging.Level
import arrow.core.test.generators.unit

fun Gen.Companion.ioOfAnyAndAny(): Gen<IO<Any, Any>> =
    oneOf(
        ioJustAny(),
        ioRaiseAnyError(),
        ioRaiseAnyException()
    )

fun Gen.Companion.ioOfAnyAndUnit(): Gen<IO<Any, Unit>> =
    oneOf(
        ioJustUnit(),
        ioRaiseAnyError(),
        ioRaiseAnyException()
    )

fun Gen.Companion.ioJustUnit(): Gen<IO<Nothing, Unit>> =
    Gen.unit().map(IO.Companion::just)

fun Gen.Companion.ioJustAny(): Gen<IO<Nothing, Any>> =
    any().map(IO.Companion::just)

fun Gen.Companion.ioRaiseAnyError(): Gen<IO<Any, Nothing>> =
    any().map(IO.Companion::raiseError)

fun Gen.Companion.ioRaiseAnyException(): Gen<IO<Nothing, Nothing>> =
    oneOf(
        throwable().map(IO.Companion::raiseException),
        fatalThrowable().map(IO.Companion::raiseException)
    )

fun Gen.Companion.any(): Gen<Any> =
    oneOf(
        string(),
        int(),
        short(),
        long(),
        float(),
        double(),
        bool(),
        byte(),
        uuid(),
        file(),
        localDate(),
        localTime(),
        localDateTime(),
        duration(),
        period(),
        throwable(),
        fatalThrowable(),
        mapOfStringAndStringGenerator(),
        uri(),
        httpMethod(),
        unit()
    )

fun Gen.Companion.mapOfStringAndStringGenerator(): Gen<Map<String, String>> =
    from(
        listOf(
            emptyMap(),
            mapOf(
                string().random().first() to string().random().first()
            ),
            mapOf(
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first()
            ),
            mapOf(
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first()
            ),
            mapOf(
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first()
            ),
            mapOf(
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first()
            ),
            mapOf(
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first(),
                string().random().first() to string().random().first()
            )
        )
    )

fun Gen.Companion.uri(): Gen<URI> =
    from(
        listOf(
            URI.create("https://sparetimedevs.com"),
            URI.create("https://www.sparetimedevs.com"),
            URI.create("https://something.sparetimedevs.com"),
            URI.create("https://something.sparetimedevs.com/another/thing"),
            URI.create("https://something.sparetimedevs.com/another/thing?query=param")
        )
    )

fun Gen.Companion.httpMethod(): Gen<HttpMethod> =
    from(
        listOf(
            HttpMethod.GET,
            HttpMethod.HEAD,
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.DELETE,
            HttpMethod.CONNECT,
            HttpMethod.OPTIONS,
            HttpMethod.TRACE
        )
    )

fun Gen.Companion.logLevel(): Gen<Level> =
    from(
        listOf(
            Level.ALL,
            Level.FINEST,
            Level.FINER,
            Level.FINE,
            Level.CONFIG,
            Level.INFO,
            Level.WARNING,
            Level.SEVERE,
            Level.OFF
        )
    )
