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

package com.sparetimedevs.pofpaf.test.implementation.azurefunctions.http

import arrow.core.Either
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.log.log
import com.sparetimedevs.pofpaf.test.implementation.general.log.Level

fun <E, A> handleHttpBlocking(
    incoming: HttpRequestMessage<out Any?>,
    context: ExecutionContext,
    domainLogic: suspend () -> Either<E, A>,
    handleSuccess: suspend (request: HttpRequestMessage<out Any?>, log: suspend (a: A) -> Either<Throwable, Unit>, a: A) -> Either<Throwable, HttpResponseMessage> =
        ::handleSuccessWithDefaultHandler,
    handleDomainError: suspend (request: HttpRequestMessage<out Any?>, log: suspend (e: E) -> Either<Throwable, Unit>, e: E) -> Either<Throwable, HttpResponseMessage> =
        ::handleDomainErrorWithDefaultHandler,
    handleSystemFailure: suspend (request: HttpRequestMessage<out Any?>, log: suspend (throwable: Throwable) -> Either<Throwable, Unit>, throwable: Throwable) -> Either<Throwable, HttpResponseMessage> =
        ::handleSystemFailureWithDefaultHandler,
    logUnrecoverableState: suspend (throwable: Throwable) -> Either<Throwable, Unit> =
        { throwable: Throwable -> log(context, Level.ERROR, "This is throwable: $throwable") }
): HttpResponseMessage =
    com.sparetimedevs.pofpaf.handler.handleBlocking(
        logic = domainLogic,
        ifSuccess = { a -> handleSuccess(incoming, { a: A -> log(context, Level.INFO, "This is a: $a") }, a) },
        ifDomainError = { e -> handleDomainError(incoming, { e: E -> log(context, Level.WARN, "This is e: $e") }, e) },
        ifSystemFailure = { throwable -> handleSystemFailure(incoming, { throwable: Throwable -> log(context, Level.ERROR, "This is throwable: $throwable") }, throwable) },
        ifUnrecoverableState = logUnrecoverableState
    )
