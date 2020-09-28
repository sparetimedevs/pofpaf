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

package com.sparetimedevs.pofpaf.handler.framework.azurefunctions

import arrow.core.Either
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.pofpaf.handler.handleBlocking
import com.sparetimedevs.pofpaf.test.generator.executionContextArb
import com.sparetimedevs.pofpaf.test.generator.httpRequestMessageGenerator
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsAnyLeft
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsAnyRight
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsEitherAnyOrAnyOrThrows
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatThrows
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatThrowsFatalThrowable
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.http.handleDomainErrorWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.http.handleSuccessWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.http.handleSystemFailureWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.log.log
import com.sparetimedevs.pofpaf.test.implementation.general.CONTENT_TYPE
import com.sparetimedevs.pofpaf.test.implementation.general.CONTENT_TYPE_APPLICATION_JSON
import com.sparetimedevs.pofpaf.test.implementation.general.ErrorResponse
import com.sparetimedevs.pofpaf.test.implementation.general.log.Level
import com.sparetimedevs.pofpaf.test.implementation.general.log.THROWABLE_MESSAGE_PREFIX
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.checkAll

class HttpHandlerKtTest : StringSpec({
    
    "Should yield an HttpResponseMessage when deterministic functions are used as handlers." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsEitherAnyOrAnyOrThrows()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Any> ->
            
            val response =
                handleBlocking(
                    f = domainLogic,
                    success = { a -> handleSuccessWithDefaultHandler(request, { a: Any -> log(context, Level.INFO, "This is a: $a") }, a) },
                    error = { e -> handleDomainErrorWithDefaultHandler(request, { e: Any -> log(context, Level.WARN, "This is e: $e") }, e) },
                    throwable = { throwable -> handleSystemFailureWithDefaultHandler(request, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    unrecoverableState = { throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }
                )
            
            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBeInRange IntRange(100, 599)
        }
    }
    
    "Should throw a Throwable when a fatal Throwable is thrown." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.suspendFunThatThrowsFatalThrowable()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Any> ->
            
            shouldThrow<Throwable> {
                handleBlocking(
                    f = domainLogic,
                    success = { a -> handleSuccessWithDefaultHandler(request, { a: Any -> log(context, Level.INFO, "This is a: $a") }, a) },
                    error = { e -> handleDomainErrorWithDefaultHandler(request, { e: Any -> log(context, Level.WARN, "This is e: $e") }, e) },
                    throwable = { throwable -> handleSystemFailureWithDefaultHandler(request, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    unrecoverableState = { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }
                )
            }
        }
    }
    
    "Should yield an HttpResponseMessage when an exception is thrown in the success supplied function." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsAnyRight()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Any> ->
            
            val response =
                handleBlocking(
                    f = domainLogic,
                    success = { a -> throwException(request, { a: Any -> log(context, Level.INFO, "This is a: $a") }, a) },
                    error = { e -> handleDomainErrorWithDefaultHandler(request, { e: Any -> log(context, Level.WARN, "This is e: $e") }, e) },
                    throwable = { throwable -> handleSystemFailureWithDefaultHandler(request, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    unrecoverableState = { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }
                )
            
            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
            response.body shouldBe ErrorResponse("$THROWABLE_MESSAGE_PREFIX $exception")
        }
    }
    
    "Should yield an HttpResponseMessage when an exception is thrown in the error supplied function." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsAnyLeft()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Any> ->
            
            val response =
                handleBlocking(
                    f = domainLogic,
                    success = { a -> handleSuccessWithDefaultHandler(request, { a: Any -> log(context, Level.INFO, "This is a: $a") }, a) },
                    error = { e -> throwException(request, { e: Any -> log(context, Level.WARN, "This is e: $e") }, e) },
                    throwable = { throwable -> handleSystemFailureWithDefaultHandler(request, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    unrecoverableState = { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }
                )
            
            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
            response.body shouldBe ErrorResponse("$THROWABLE_MESSAGE_PREFIX $exception")
        }
    }
    
    "Should throw a Throwable when any exception is thrown in the throwable supplied function." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.suspendFunThatThrows(),
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Any> ->
            
            shouldThrow<Throwable> {
                handleBlocking(
                    f = domainLogic,
                    success = { a -> handleSuccessWithDefaultHandler(request, { a: Any -> log(context, Level.INFO, "This is a: $a") }, a) },
                    error = { e -> handleDomainErrorWithDefaultHandler(request, { e: Any -> log(context, Level.WARN, "This is e: $e") }, e) },
                    throwable = { throwable -> throwException(request, { throwable: Any -> log(context, Level.WARN, "This is throwable: $throwable") }, throwable) },
                    unrecoverableState = { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }
                )
            }
        }
    }
})

private val exception = RuntimeException("An Exception is thrown while handling the result of the domain f.")

@Suppress("UNUSED_PARAMETER")
private suspend fun <T> throwException(
    request: HttpRequestMessage<out Any?>,
    log: suspend (t: T) -> Either<Throwable, Unit>,
    t: T
): Either<Throwable, HttpResponseMessage> =
    throw exception
