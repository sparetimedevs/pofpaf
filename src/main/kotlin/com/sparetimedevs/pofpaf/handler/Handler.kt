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
import arrow.core.nonFatalOrThrow
import arrow.core.right
import arrow.fx.coroutines.ComputationPool
import arrow.fx.coroutines.Environment
import kotlin.coroutines.CoroutineContext

/**
 * Generic handler that can handle any logic.
 *
 * This function can produce a deterministic result when used correctly.
 *
 * @param f the function that needs to be handled.
 * @param success the function to apply if [f] yields a success of type [A].
 * @param error the function to apply if [f] yields an error of type [E].
 * @param throwable the function to apply if [f] throws a [Throwable].
 * Throwing any [Throwable] in the [throwable] function will render the [handle] function nondeterministic.
 * @param unrecoverableState the function to apply if [handle] is in an unrecoverable state.
 * @return the result of applying the [handle] function.
 */
suspend inline fun <E, A, B> handle(
    f: suspend () -> Either<E, A>,
    success: suspend (a: A) -> Either<Throwable, B>,
    error: suspend (e: E) -> Either<Throwable, B>,
    throwable: suspend (throwable: Throwable) -> Either<Throwable, B>,
    unrecoverableState: suspend (throwable: Throwable) -> Either<Throwable, Unit>
): B =
    Either.catchInlined(f)
        .fold(
            { t: Throwable ->
                throwable(t)
            },
            {
                it.fold(
                    { e: E ->
                        handleItSafely { error(e) }
                    },
                    { a: A ->
                        handleItSafely { success(a) }
                    }
                )
            }
        )
        .fold(
            { t: Throwable ->
                throwable(t)
            },
            { b: B ->
                b.right()
            }
        )
        .fold(
            { t: Throwable ->
                unrecoverableState(t)
                throw t
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
 * @param f the function that needs to be handled.
 * @param success the function to apply if [f] yields a success of type [A].
 * @param error the function to apply if [f] yields an error of type [E].
 * @param throwable the function to apply if [f] throws a [Throwable].
 * Throwing any [Throwable] in the [throwable] function will render the [handleBlocking] function nondeterministic.
 * @param unrecoverableState the function to apply if [handleBlocking] is in an unrecoverable state.
 * @return the result of applying the [handleBlocking] function.
 */
fun <E, A, B> handleBlocking(
    ctx: CoroutineContext = ComputationPool,
    f: suspend () -> Either<E, A>,
    success: suspend (a: A) -> Either<Throwable, B>,
    error: suspend (e: E) -> Either<Throwable, B>,
    throwable: suspend (throwable: Throwable) -> Either<Throwable, B>,
    unrecoverableState: suspend (throwable: Throwable) -> Either<Throwable, Unit> = { Unit.right() }
): B =
    Environment(ctx)
        .unsafeRunSync(
            suspend {
                handle(
                    f = f,
                    success = success,
                    error = error,
                    throwable = throwable,
                    unrecoverableState = unrecoverableState
                )
            }
        )

@PublishedApi
internal suspend inline fun <R> Either.Companion.catchInlined(f: suspend () -> R): Either<Throwable, R> =
    try {
        f().right()
    } catch (t: Throwable) {
        t.nonFatalOrThrow().left()
    }

@PublishedApi
internal suspend inline fun <A> handleItSafely(f: suspend () -> Either<Throwable, A>): Either<Throwable, A> =
    Either.catchInlined { f() }
        .fold(
            {
                it.left()
            },
            {
                it
            }
        )
