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

package com.sparetimedevs.pofpaf.test.implementation.pseudo.http

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.sparetimedevs.pofpaf.handler.handle

suspend fun httpEndpoint(request: String = "Hello?") =
    handle(
        f = {
            if (request == "Hello?") "HELLO WORLD!".right()
            else DomainError("No \"Hello?\"").left()
        },
        success = { a -> handleSuccessWithDefaultHandler({ a: Any -> log(Level.INFO, "This is a: $a") }, a) },
        error = { e -> handleDomainErrorWithDefaultHandler({ e: Any -> log(Level.WARN, "This is e: $e") }, e) },
        throwable = { throwable -> handleSystemFailureWithDefaultHandler({ throwable: Throwable -> log(Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
        unrecoverableState = { throwable -> log(Level.ERROR, "Log the throwable: $throwable.") }
    )

suspend fun main() {
    println("httpEndpoint().status = ${httpEndpoint().status}")
}

@Suppress("UNUSED_PARAMETER")
suspend fun <A> handleSuccessWithDefaultHandler(log: suspend (a: A) -> Either<Throwable, Unit>, a: A): Either<Throwable, Response> =
    Either.catch { createResponse(a) }

private fun <A> createResponse(a: A): Response =
    Response.Builder(HttpStatus.OK)
        .header(
            CONTENT_TYPE,
            CONTENT_TYPE_APPLICATION_JSON
        )
        .body(a)
        .build()

@Suppress("UNUSED_PARAMETER")
suspend fun <E> handleDomainErrorWithDefaultHandler(log: suspend (e: E) -> Either<Throwable, Unit>, e: E): Either<Throwable, Response> =
    createErrorResponse(HttpStatus.NOT_FOUND, ErrorResponse("$ERROR_MESSAGE_PREFIX $e"))

suspend fun handleSystemFailureWithDefaultHandler(log: suspend (throwable: Throwable) -> Either<Throwable, Unit>, throwable: Throwable): Either<Throwable, Response> =
    log(throwable)
        .flatMap {
            createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponse("$THROWABLE_MESSAGE_PREFIX $throwable"))
        }

suspend fun createErrorResponse(httpStatus: HttpStatus, errorResponse: ErrorResponse): Either<Throwable, Response> =
    Either.catch {
        Response.Builder(httpStatus)
            .header(
                CONTENT_TYPE,
                CONTENT_TYPE_APPLICATION_JSON
            )
            .body(errorResponse)
            .build()
    }

suspend fun log(level: Level, message: String): Either<Throwable, Unit> =
    Unit.right() // Should implement logging.

enum class HttpStatus(val value: Int) {
    OK(200),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500)
}

class Response private constructor(
    val status: HttpStatus,
    val headers: Map<String, String>,
    val body: Any?
) {
    
    data class Builder(
        val status: HttpStatus,
        var headers: Map<String, String> = emptyMap(),
        var body: Any? = null
    ) {
        fun header(key: String, value: String) = apply { this.headers = this.headers + mapOf<String, String>(key to value) }
        fun body(body: Any?) = apply { this.body = body }
        fun build() = Response(status, headers, body)
    }
}

const val CONTENT_TYPE = "Content-Type"
const val CONTENT_TYPE_APPLICATION_JSON = "application/json"
const val ERROR_MESSAGE_PREFIX = "An error has occurred. The error is:"
const val THROWABLE_MESSAGE_PREFIX = "An exception was thrown. The exception is:"
data class DomainError(val errorMessage: String)
data class ErrorResponse(val errorMessage: String)
enum class Level { TRACE, DEBUG, INFO, WARN, ERROR }
