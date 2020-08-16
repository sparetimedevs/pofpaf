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
import com.sparetimedevs.pofpaf.log.Level
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.log.log

fun <E, A> handleHttpBlocking(
    incoming: HttpRequestMessage<out Any?>,
    context: ExecutionContext,
    domainLogic: suspend () -> Either<E, A>,
    handleSuccess: suspend (request: HttpRequestMessage<out Any?>, log: suspend (level: Level, message: String) -> Either<Throwable, Unit>, a: A) -> Either<Throwable, HttpResponseMessage> =
        ::handleSuccessWithDefaultHandler,
    handleDomainError: suspend (request: HttpRequestMessage<out Any?>, log: suspend (level: Level, message: String) -> Either<Throwable, Unit>, e: E) -> Either<Throwable, HttpResponseMessage> =
        ::handleDomainErrorWithDefaultHandler,
    handleSystemFailure: suspend (request: HttpRequestMessage<out Any?>, log: suspend (level: Level, message: String) -> Either<Throwable, Unit>, throwable: Throwable) -> Either<Throwable, HttpResponseMessage> =
        ::handleSystemFailureWithDefaultHandler,
    log: suspend (level: Level, message: String) -> Either<Throwable, Unit> =
        { level, message -> log(context, level, message) }
): HttpResponseMessage =
    com.sparetimedevs.pofpaf.handler.handleBlocking(
        domainLogic = domainLogic,
        handleSuccess = { a -> handleSuccess(incoming, log, a) },
        handleDomainError = { e -> handleDomainError(incoming, log, e) },
        handleSystemFailure = { throwable -> handleSystemFailure(incoming, log, throwable) },
        log = log
    )
