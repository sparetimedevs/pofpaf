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

import arrow.fx.IO
import com.microsoft.azure.functions.HttpMethod
import io.kotlintest.properties.Gen
import java.net.URI
import java.util.Optional
import java.util.logging.Level

internal fun Gen.Companion.io(): Gen<IO<Any, Any>> =
        oneOf(
                ioJustAny(),
                ioRaiseAnyError(),
                ioRaiseAnyException()
        )

internal fun Gen.Companion.ioJustAny(): Gen<IO<Nothing, Any>> =
        any().map(IO.Companion::just)

internal fun Gen.Companion.ioRaiseAnyError(): Gen<IO<Any, Nothing>> =
        any().map(IO.Companion::raiseError)

internal fun Gen.Companion.ioRaiseAnyException(): Gen<IO<Nothing, Nothing>> =
        throwable().map(IO.Companion::raiseException)

private fun Gen.Companion.any(): Gen<Any> =
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
                mapOfStringAndStringGenerator(),
                uri(),
                httpMethod(),
                optionalString()
        )

private fun Gen.Companion.throwable(): Gen<Throwable> =
        from(
                listOf(
                        Error(),
                        Exception(),
                        RuntimeException(),
                        IllegalArgumentException(),
                        IllegalStateException(),
                        IndexOutOfBoundsException(),
                        UnsupportedOperationException(),
                        ArithmeticException(),
                        NumberFormatException(),
                        NullPointerException(),
                        ClassCastException(),
                        AssertionError(),
                        NoSuchElementException(),
                        ConcurrentModificationException()
                )
        )

internal fun Gen.Companion.mapOfStringAndStringGenerator(): Gen<Map<String, String>> =
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

internal fun Gen.Companion.uri(): Gen<URI> =
        from(
                listOf(
                        URI.create("https://sparetimedevs.com"),
                        URI.create("https://www.sparetimedevs.com"),
                        URI.create("https://something.sparetimedevs.com"),
                        URI.create("https://something.sparetimedevs.com/another/thing"),
                        URI.create("https://something.sparetimedevs.com/another/thing?query=param")
                )
        )

internal fun Gen.Companion.httpMethod(): Gen<HttpMethod> =
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

internal fun Gen.Companion.optionalString(): Gen<Optional<String>> =
        oneOf(
                string().map { Optional.of(it) },
                EmptyOptionalGenerator()
        )

private class EmptyOptionalGenerator : Gen<Optional<String>> {
    override fun constants(): List<Optional<String>> = emptyList()
    override fun random(): Sequence<Optional<String>> = generateSequence {
        Optional.empty<String>()
    }
}

internal fun Gen.Companion.logLevel(): Gen<Level> =
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
