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

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.microsoft.azure.functions.HttpMethod
import com.sparetimedevs.pofpaf.test.implementation.general.log.Level
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

fun Arb.Companion.suspendFunThatReturnsEitherAnyOrAnyOrThrows(): Arb<suspend () -> Either<Any, Any>> =
    choice(
        suspendFunThatReturnsAnyRight(),
        suspendFunThatReturnsAnyLeft(),
        suspendFunThatThrows()
    )

fun Arb.Companion.suspendFunThatReturnsEitherAnyOrUnitOrThrows(): Arb<suspend () -> Either<Any, Unit>> =
    choice(
        suspendFunThatReturnsUnitRight() as Arb<suspend () -> Either<Any, Unit>>,
        suspendFunThatReturnsAnyLeft() as Arb<suspend () -> Either<Any, Unit>>,
        suspendFunThatThrows() as Arb<suspend () -> Either<Any, Unit>>
    )

fun Arb.Companion.suspendFunThatReturnsUnitRight(): Arb<suspend () -> Either<Any, Unit>> =
    unit().map { suspend { it.right() } }

fun Arb.Companion.suspendFunThatReturnsAnyRight(): Arb<suspend () -> Either<Any, Any>> =
    any().map { suspend { it.right() } }

fun Arb.Companion.suspendFunThatReturnsAnyLeft(): Arb<suspend () -> Either<Any, Any>> =
    any().map { suspend { it.left() } }

fun Arb.Companion.suspendFunThatThrows(): Arb<suspend () -> Either<Any, Any>> =
    throwable().map { suspend { throw it } } as Arb<suspend () -> Either<Any, Any>>

fun Arb.Companion.suspendFunThatThrowsFatalThrowable(): Arb<suspend () -> Either<Any, Any>> =
    fatalThrowable().map { suspend { throw it } } as Arb<suspend () -> Either<Any, Any>>

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
        MyVirtualMachineError(),
        ThreadDeath(),
        InterruptedException(),
        LinkageError()
    )

class MyVirtualMachineError : VirtualMachineError()

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
        Level.INFO,
        Level.DEBUG,
        Level.WARN,
        Level.ERROR
    ).exhaustive()

fun Arb.Companion.stringOrNull(): Arb<String?> =
    choice(
        string() as Arb<String?>,
        create { null } as Arb<String?>
    )
