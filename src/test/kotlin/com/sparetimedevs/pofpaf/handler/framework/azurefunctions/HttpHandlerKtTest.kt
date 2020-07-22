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
import com.sparetimedevs.pofpaf.log.Level
import com.sparetimedevs.pofpaf.log.THROWABLE_MESSAGE_PREFIX
import com.sparetimedevs.pofpaf.test.generator.executionContextArb
import com.sparetimedevs.pofpaf.test.generator.httpRequestMessageGenerator
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsAnyLeft
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsAnyRight
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsEitherAnyOrAnyOrThrows
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatThrowsFatalThrowable
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.http.handleDomainErrorWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.http.handleSuccessWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.http.handleSystemFailureWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.log.log
import com.sparetimedevs.pofpaf.test.implementation.general.CONTENT_TYPE
import com.sparetimedevs.pofpaf.test.implementation.general.CONTENT_TYPE_APPLICATION_JSON
import com.sparetimedevs.pofpaf.test.implementation.general.ErrorResponse
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
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            val response =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleSuccessWithDefaultHandler(request, log, a) },
                    handleDomainError = { e -> handleDomainErrorWithDefaultHandler(request, log, e) },
                    handleSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, log, throwable) },
                    handleHandlerFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, log, throwable) },
                    log = log
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
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            shouldThrow<Throwable> {
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleSuccessWithDefaultHandler(request, log, a) },
                    handleDomainError = { e -> handleDomainErrorWithDefaultHandler(request, log, e) },
                    handleSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, log, throwable) },
                    handleHandlerFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, log, throwable) },
                    log = log
                )
            }
        }
    }
    
    "Should yield an HttpResponseMessage when an exception is thrown in the handleSuccess supplied function." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsAnyRight()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Any> ->
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            val response =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> throwException(request, log, a) },
                    handleDomainError = { e -> handleDomainErrorWithDefaultHandler(request, log, e) },
                    handleSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, log, throwable) },
                    handleHandlerFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, log, throwable) },
                    log = log
                )
            
            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
            response.body shouldBe ErrorResponse("$THROWABLE_MESSAGE_PREFIX $exception")
        }
    }
    
    "Should yield an HttpResponseMessage when an exception is thrown in the handleDomainError supplied function." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsAnyLeft()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Any> ->
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            val response =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleSuccessWithDefaultHandler(request, log, a) },
                    handleDomainError = { e -> throwException(request, log, e) },
                    handleSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, log, throwable) },
                    handleHandlerFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, log, throwable) },
                    log = log
                )
            
            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
            response.body shouldBe ErrorResponse("$THROWABLE_MESSAGE_PREFIX $exception")
        }
    }
    
    "Should yield an HttpResponseMessage when an exception is thrown in the handleSystemFailure supplied function." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsAnyLeft()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Any> ->
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            val response =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleSuccessWithDefaultHandler(request, log, a) },
                    handleDomainError = { e -> throwException(request, log, e) },
                    handleSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, log, throwable) },
                    handleHandlerFailure = { throwable -> handleSystemFailureWithDefaultHandler(request, log, throwable) },
                    log = log
                )
            
            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
            response.body shouldBe ErrorResponse("$THROWABLE_MESSAGE_PREFIX $exception")
        }
    }
    
    "Should throw a Throwable when any exception is thrown in the handleHandlerFailure supplied function." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsEitherAnyOrAnyOrThrows()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Any> ->
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            shouldThrow<Throwable> {
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { throwable -> throwException(request, log, throwable) },
                    handleDomainError = { throwable -> throwException(request, log, throwable) },
                    handleSystemFailure = { throwable -> throwException(request, log, throwable) },
                    handleHandlerFailure = { throwable -> throwException(request, log, throwable) },
                    log = log
                )
            }
        }
    }
})

private val exception = RuntimeException("An Exception is thrown while handling the result of the domain logic.")

@Suppress("UNUSED_PARAMETER")
private suspend fun <T> throwException(
    request: HttpRequestMessage<out Any?>,
    log: suspend (level: Level, message: String) -> Either<Throwable, Unit>,
    t: T
): Either<Throwable, HttpResponseMessage> =
    throw exception
