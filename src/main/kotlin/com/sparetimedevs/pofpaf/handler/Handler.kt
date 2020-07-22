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

package com.sparetimedevs.pofpaf.handler

import arrow.core.Either
import arrow.core.right
import arrow.fx.coroutines.ComputationPool
import arrow.fx.coroutines.Environment
import com.sparetimedevs.pofpaf.log.Level
import com.sparetimedevs.pofpaf.log.THROWABLE_MESSAGE_PREFIX
import kotlin.coroutines.CoroutineContext

/**
 * Generic handler that can handle any domain logic, including incoming events or requests.
 *
 * This function can produce a deterministic result when used correctly.
 *
 * @property domainLogic the function to perform domain logic.
 * @property handleSuccess the function to handle the case when the domain logic yields a success.
 * @property handleDomainError the function to handle the case when the domain logic yields a domain error.
 * @property handleSystemFailure the function to handle the case when the domain logic yields a system failure.
 * @property handleHandlerFailure the function to handle the case when the [handleSuccess], [handleDomainError] or [handleSystemFailure] functions throw a [Throwable].
 * Throwing any [Throwable] in the handleHandlerFailure function will render the [handle] function nondeterministic.
 * @property log the function to log in case of an unrecoverable state.
 */
suspend fun <E, A, B> handle(
    domainLogic: suspend () -> Either<E, A>,
    handleSuccess: suspend (a: A) -> Either<Throwable, B>,
    handleDomainError: suspend (e: E) -> Either<Throwable, B>,
    handleSystemFailure: suspend (throwable: Throwable) -> Either<Throwable, B>,
    handleHandlerFailure: suspend (throwable: Throwable) -> Either<Throwable, B> = handleSystemFailure,
    log: suspend (level: Level, message: String) -> Either<Throwable, Unit>
): B =
    Either.catch { domainLogic() }
        .fold(
            { handleItSafely { handleSystemFailure(it) } },
            {
                it.fold(
                    { e: E ->
                        handleItSafely { handleDomainError(e) }
                    },
                    { a: A ->
                        handleItSafely { handleSuccess(a) }
                    }
                )
            }
        )
        .fold(
            { handleHandlerFailure(it) },
            { it.right() }
        )
        .fold(
            { throwable: Throwable ->
                log(Level.ERROR, "$THROWABLE_MESSAGE_PREFIX $throwable")
                throw throwable
            },
            { it }
        )

/**
 * Generic handler that can handle any domain logic in a blocking fashion, including incoming events or requests.
 *
 * This function can produce a deterministic result when used correctly.
 *
 * @property domainLogic the function to perform domain logic.
 * @property handleSuccess the function to handle the case when the domain logic yields a success.
 * @property handleDomainError the function to handle the case when the domain logic yields a domain error.
 * @property handleSystemFailure the function to handle the case when the domain logic yields a system failure.
 * @property handleHandlerFailure the function to handle the case when the [handleSuccess], [handleDomainError] or [handleSystemFailure] functions throw a [Throwable].
 * Throwing any [Throwable] in the handleHandlerFailure function will render the [handle] function nondeterministic.
 * @property log the function to log in case of an unrecoverable state.
 */
fun <E, A, B> handleBlocking(
    ctx: CoroutineContext = ComputationPool,
    domainLogic: suspend () -> Either<E, A>,
    handleSuccess: suspend (a: A) -> Either<Throwable, B>,
    handleDomainError: suspend (e: E) -> Either<Throwable, B>,
    handleSystemFailure: suspend (throwable: Throwable) -> Either<Throwable, B>,
    handleHandlerFailure: suspend (throwable: Throwable) -> Either<Throwable, B> = handleSystemFailure,
    log: suspend (level: Level, message: String) -> Either<Throwable, Unit>
): B =
    Environment(ctx)
        .unsafeRunSync(
            suspend {
                handle(
                    domainLogic = domainLogic,
                    handleSuccess = handleSuccess,
                    handleDomainError = handleDomainError,
                    handleSystemFailure = handleSystemFailure,
                    handleHandlerFailure = handleHandlerFailure,
                    log = log
                )
            }
        )
