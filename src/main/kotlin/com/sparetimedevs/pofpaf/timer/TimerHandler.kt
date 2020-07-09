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

package com.sparetimedevs.pofpaf.timer

import arrow.core.Either
import arrow.core.right
import com.microsoft.azure.functions.ExecutionContext
import com.sparetimedevs.pofpaf.http.THROWABLE_MESSAGE_PREFIX
import com.sparetimedevs.pofpaf.log.log
import com.sparetimedevs.pofpaf.run.unsafeRunSync
import com.sparetimedevs.pofpaf.util.handleItSafely
import java.util.logging.Level

fun <E> handleTimer(
    timerInfo: String,
    context: ExecutionContext,
    domainLogic: suspend () -> Either<E, Unit>,
    handleSuccess: suspend (timerInfo: String, context: ExecutionContext) -> Either<Throwable, Unit> =
        ::handleSuccessWithDefaultHandler,
    handleDomainError: suspend (timerInfo: String, context: ExecutionContext, e: E) -> Either<Throwable, Unit> =
        ::handleDomainErrorWithDefaultHandler,
    handleSystemFailure: suspend (timerInfo: String, context: ExecutionContext, throwable: Throwable) -> Either<Throwable, Unit> =
        ::handleSystemFailureWithDefaultHandler
): Unit =
    suspend {
        doHandleTimer(
            timerInfo,
            context,
            domainLogic,
            handleSuccess,
            handleDomainError,
            handleSystemFailure
        )
    }.unsafeRunSync()

suspend fun <E> doHandleTimer(
    timerInfo: String,
    context: ExecutionContext,
    domainLogic: suspend () -> Either<E, Unit>,
    handleSuccess: suspend (timerInfo: String, context: ExecutionContext) -> Either<Throwable, Unit>,
    handleDomainError: suspend (timerInfo: String, context: ExecutionContext, e: E) -> Either<Throwable, Unit>,
    handleSystemFailure: suspend (timerInfo: String, context: ExecutionContext, throwable: Throwable) -> Either<Throwable, Unit>
): Unit =
    Either.catch {
        domainLogic()
    }
        .fold(
            {
                handleItSafely { handleSystemFailure(timerInfo, context, it) }
            },
            {
                it.fold(
                    { e: E ->
                        handleItSafely { handleDomainError(timerInfo, context, e) }
                    },
                    { _ ->
                        handleItSafely { handleSuccess(timerInfo, context) }
                    }
                )
            }
        )
        .fold({ handleSystemFailureWithDefaultHandler(timerInfo, context, it) }, { it.right() })
        .fold(
            { throwable: Throwable ->
                log(context, Level.SEVERE, "$THROWABLE_MESSAGE_PREFIX $throwable. ${throwable.message}")
                throw throwable
            },
            { }
        )
