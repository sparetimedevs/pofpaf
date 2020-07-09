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

package com.sparetimedevs.pofpaf.http

import arrow.core.Either
import arrow.core.right
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.sparetimedevs.pofpaf.log.log
import com.sparetimedevs.pofpaf.run.unsafeRunSync
import com.sparetimedevs.pofpaf.util.handleItSafely
import java.util.logging.Level

fun <E, A> handleHttp(
    request: HttpRequestMessage<out Any?>,
    context: ExecutionContext,
    domainLogic: suspend () -> Either<E, A>,
    handleSuccess: suspend (request: HttpRequestMessage<out Any?>, context: ExecutionContext, a: A) -> Either<Throwable, HttpResponseMessage> =
        ::handleSuccessWithDefaultHandler,
    handleDomainError: suspend (request: HttpRequestMessage<out Any?>, context: ExecutionContext, e: E) -> Either<Throwable, HttpResponseMessage> =
        ::handleDomainErrorWithDefaultHandler,
    handleSystemFailure: suspend (request: HttpRequestMessage<out Any?>, context: ExecutionContext, throwable: Throwable) -> Either<Throwable, HttpResponseMessage> =
        ::handleSystemFailureWithDefaultHandler
): HttpResponseMessage =
    suspend {
        doHandleHttp(
            request,
            context,
            domainLogic,
            handleSuccess,
            handleDomainError,
            handleSystemFailure
        )
    }.unsafeRunSync()

suspend fun <E, A> doHandleHttp(
    request: HttpRequestMessage<out Any?>,
    context: ExecutionContext,
    domainLogic: suspend () -> Either<E, A>,
    handleSuccess: suspend (request: HttpRequestMessage<out Any?>, context: ExecutionContext, a: A) -> Either<Throwable, HttpResponseMessage>,
    handleDomainError: suspend (request: HttpRequestMessage<out Any?>, context: ExecutionContext, e: E) -> Either<Throwable, HttpResponseMessage>,
    handleSystemFailure: suspend (request: HttpRequestMessage<out Any?>, context: ExecutionContext, throwable: Throwable) -> Either<Throwable, HttpResponseMessage>
): HttpResponseMessage =
    Either.catch {
        domainLogic()
    }
        .fold(
            {
                handleItSafely { handleSystemFailure(request, context, it) }
            },
            {
                it.fold(
                    { e: E ->
                        handleItSafely { handleDomainError(request, context, e) }
                    },
                    { a: A ->
                        handleItSafely { handleSuccess(request, context, a) }
                    }
                )
            }
        )
        .fold({ handleSystemFailureWithDefaultHandler(request, context, it) }, { it.right() })
        .fold(
            { throwable: Throwable ->
                log(context, Level.SEVERE, "$THROWABLE_MESSAGE_PREFIX $throwable. ${throwable.message}")
                throw throwable
            },
            { it }
        )
