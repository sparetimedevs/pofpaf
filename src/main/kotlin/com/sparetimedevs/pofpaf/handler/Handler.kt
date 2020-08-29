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
import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.ComputationPool
import arrow.fx.coroutines.Environment
import kotlin.coroutines.CoroutineContext

/**
 * Generic handler that can handle any logic.
 *
 * This function can produce a deterministic result when used correctly.
 *
 * @param logic the function to apply logic that needs to be handled.
 * @param ifSuccess the function to apply if [logic] yields a success of type [A].
 * @param ifDomainError the function to apply if [logic] yields a domain error of type [E].
 * @param ifSystemFailure the function to apply if [logic] throws a [Throwable].
 * @param ifHandlingCaseThrows the function to apply if the [ifSuccess], [ifDomainError] or [ifSystemFailure] function throw a [Throwable].
 * Throwing any [Throwable] in the [ifHandlingCaseThrows] function will render the [handle] function nondeterministic.
 * @param ifUnrecoverableState the function to apply if [handle] is in an unrecoverable state.
 * @return the result of applying the [handle] function.
 */
suspend fun <E, A, B> handle(
    logic: suspend () -> Either<E, A>,
    ifSuccess: suspend (a: A) -> Either<Throwable, B>,
    ifDomainError: suspend (e: E) -> Either<Throwable, B>,
    ifSystemFailure: suspend (throwable: Throwable) -> Either<Throwable, B>,
    ifHandlingCaseThrows: suspend (throwable: Throwable) -> Either<Throwable, B> = ifSystemFailure,
    ifUnrecoverableState: suspend (throwable: Throwable) -> Either<Throwable, Unit> = { Unit.right() }
): B =
    Either.catch { logic() }
        .fold(
            { throwable: Throwable ->
                handleItSafely { ifSystemFailure(throwable) }
            },
            {
                it.fold(
                    { e: E ->
                        handleItSafely { ifDomainError(e) }
                    },
                    { a: A ->
                        handleItSafely { ifSuccess(a) }
                    }
                )
            }
        )
        .fold(
            { throwable: Throwable ->
                ifHandlingCaseThrows(throwable)
            },
            { b: B ->
                b.right()
            }
        )
        .fold(
            { throwable: Throwable ->
                ifUnrecoverableState(throwable)
                throw throwable
            },
            { b: B ->
                b
            }
        )

/**
 * Generic handler that can handle any logic in a blocking fashion.
 *
 * This function can produce a deterministic result when used correctly.
 *
 * @param ctx the [CoroutineContext] to run the function on.
 * @param logic the function to apply logic that needs to be handled.
 * @param ifSuccess the function to apply if [logic] yields a success of type [A].
 * @param ifDomainError the function to apply if [logic] yields a domain error of type [E].
 * @param ifSystemFailure the function to apply if [logic] throws a [Throwable].
 * @param ifHandlingCaseThrows the function to apply if the [ifSuccess], [ifDomainError] or [ifSystemFailure] function throw a [Throwable].
 * Throwing any [Throwable] in the [ifHandlingCaseThrows] function will render the [handle] function nondeterministic.
 * @param ifUnrecoverableState the function to apply if [handleBlocking] is in an unrecoverable state.
 * @return the result of applying the [handleBlocking] function.
 */
fun <E, A, B> handleBlocking(
    ctx: CoroutineContext = ComputationPool,
    logic: suspend () -> Either<E, A>,
    ifSuccess: suspend (a: A) -> Either<Throwable, B>,
    ifDomainError: suspend (e: E) -> Either<Throwable, B>,
    ifSystemFailure: suspend (throwable: Throwable) -> Either<Throwable, B>,
    ifHandlingCaseThrows: suspend (throwable: Throwable) -> Either<Throwable, B> = ifSystemFailure,
    ifUnrecoverableState: suspend (throwable: Throwable) -> Either<Throwable, Unit> = { Unit.right() }
): B =
    Environment(ctx)
        .unsafeRunSync(
            suspend {
                handle(
                    logic = logic,
                    ifSuccess = ifSuccess,
                    ifDomainError = ifDomainError,
                    ifSystemFailure = ifSystemFailure,
                    ifHandlingCaseThrows = ifHandlingCaseThrows,
                    ifUnrecoverableState = ifUnrecoverableState
                )
            }
        )

internal suspend fun <A> handleItSafely(f: suspend () -> Either<Throwable, A>): Either<Throwable, A> =
    Either.catch { f() }
        .fold(
            {
                it.left()
            },
            {
                it
            }
        )
