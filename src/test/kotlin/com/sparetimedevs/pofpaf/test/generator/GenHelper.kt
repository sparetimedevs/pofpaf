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

package com.sparetimedevs.pofpaf.test.generator

import arrow.fx.IO
import com.microsoft.azure.functions.HttpMethod
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.create
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.file
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.localTime
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.period
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid
import io.kotest.property.exhaustive.exhaustive
import java.net.URI
import java.util.logging.Level

fun Arb.Companion.ioOfAnyAndAny(): Arb<IO<Any, Any>> =
    choice(
        ioJustAny() as Arb<IO<Any, Any>>,
        ioRaiseAnyError() as Arb<IO<Any, Any>>,
        ioRaiseAnyException() as Arb<IO<Any, Any>>
    )

fun Arb.Companion.ioOfAnyAndUnit(): Arb<IO<Any, Unit>> =
    choice(
        ioJustUnit() as Arb<IO<Any, Unit>>,
        ioRaiseAnyError() as Arb<IO<Any, Unit>>,
        ioRaiseAnyException() as Arb<IO<Any, Unit>>
    )

fun Arb.Companion.ioJustUnit(): Arb<IO<Nothing, Unit>> =
    unit().map(IO.Companion::just)

fun Arb.Companion.ioJustAny(): Arb<IO<Nothing, Any>> =
    any().map(IO.Companion::just)

fun Arb.Companion.ioRaiseAnyError(): Arb<IO<Any, Nothing>> =
    any().map(IO.Companion::raiseError)

fun Arb.Companion.ioRaiseAnyException(): Arb<IO<Nothing, Nothing>> =
    choice<IO<Nothing, Nothing>>(
        throwable().map(IO.Companion::raiseException),
        fatalThrowable().map(IO.Companion::raiseException)
    )

fun Arb.Companion.throwable(): Arb<Throwable> =
    element(
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

fun Arb.Companion.fatalThrowable(): Arb<Throwable> =
    element(
        Error(),
        ThreadDeath(),
        StackOverflowError(),
        OutOfMemoryError(),
        InterruptedException()
    )

fun Arb.Companion.any(): Arb<Any> =
    choice(
        string() as Arb<Any>,
        int() as Arb<Any>,
        short() as Arb<Any>,
        long() as Arb<Any>,
        float() as Arb<Any>,
        double() as Arb<Any>,
        bool() as Arb<Any>,
        byte() as Arb<Any>,
        uuid() as Arb<Any>,
        file() as Arb<Any>,
        localDate() as Arb<Any>,
        localTime() as Arb<Any>,
        localDateTime() as Arb<Any>,
        period() as Arb<Any>,
        throwable() as Arb<Any>,
        fatalThrowable() as Arb<Any>,
        mapOfStringAndStringGenerator() as Arb<Any>,
        uri() as Arb<Any>,
        httpMethod() as Arb<Any>,
        unit() as Arb<Any>
    )

fun Arb.Companion.unit(): Arb<Unit> =
    create { Unit }

fun Arb.Companion.mapOfStringAndStringGenerator(): Arb<Map<String, String>> =
    element(
        listOf(
            emptyMap(),
            mapOf(
                string().next() to string().next()
            ),
            mapOf(
                string().next() to string().next(),
                string().next() to string().next()
            ),
            mapOf(
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next()
            ),
            mapOf(
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next()
            ),
            mapOf(
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next()
            ),
            mapOf(
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next(),
                string().next() to string().next()
            )
        )
    )

fun Arb.Companion.uri(): Arb<URI> =
    element(
        listOf(
            URI.create("https://sparetimedevs.com"),
            URI.create("https://www.sparetimedevs.com"),
            URI.create("https://something.sparetimedevs.com"),
            URI.create("https://something.sparetimedevs.com/another/thing"),
            URI.create("https://something.sparetimedevs.com/another/thing?query=param")
        )
    )

fun Arb.Companion.httpMethod(): Arb<HttpMethod> =
    element(
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

fun Exhaustive.Companion.logLevel(): Exhaustive<Level> =
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
    ).exhaustive()

fun Arb.Companion.stringOrNull(): Arb<String?> =
    choice<String?>(
        string() as Arb<String?>,
        create { null } as Arb<String?>
    )
