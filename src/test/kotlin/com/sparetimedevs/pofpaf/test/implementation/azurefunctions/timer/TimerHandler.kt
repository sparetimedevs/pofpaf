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

package com.sparetimedevs.pofpaf.test.implementation.azurefunctions.timer

import arrow.core.Either
import com.microsoft.azure.functions.ExecutionContext
import com.sparetimedevs.pofpaf.handler.handleBlocking
import com.sparetimedevs.pofpaf.log.Level
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.log.log

fun <E> handleTimer(
    timerInfo: String,
    context: ExecutionContext,
    domainLogic: suspend () -> Either<E, Unit>,
    handleSuccess: suspend (timerInfo: String, log: suspend (level: Level, message: String) -> Either<Throwable, Unit>) -> Either<Throwable, Unit> =
        ::handleSuccessWithDefaultHandler,
    handleDomainError: suspend (timerInfo: String, log: suspend (level: Level, message: String) -> Either<Throwable, Unit>, e: E) -> Either<Throwable, Unit> =
        ::handleDomainErrorWithDefaultHandler,
    handleSystemFailure: suspend (timerInfo: String, log: suspend (level: Level, message: String) -> Either<Throwable, Unit>, throwable: Throwable) -> Either<Throwable, Unit> =
        ::handleSystemFailureWithDefaultHandler,
    log: suspend (level: Level, message: String) -> Either<Throwable, Unit> =
        { level, message -> log(context, level, message) }
): Unit =
    handleBlocking(
        domainLogic = domainLogic,
        handleSuccess = { _ -> handleSuccess(timerInfo, log) },
        handleDomainError = { e -> handleDomainError(timerInfo, log, e) },
        handleSystemFailure = { throwable -> handleSystemFailure(timerInfo, log, throwable) },
        log = log
    )
