package com.sparetimedevs.pofpaf.handler

import arrow.core.Either
import arrow.core.left

suspend fun <A> handleItSafely(f: suspend () -> Either<Throwable, A>): Either<Throwable, A> =
    Either.catch { f() }
        .fold(
            {
                it.left()
            },
            {
                it
            }
        )
