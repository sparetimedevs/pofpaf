package com.sparetimedevs.pofpaf.util

import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.fix
import arrow.core.left

inline fun <A, B, C> EitherOf<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
    fix().let {
        when (it) {
            is Either.Right -> f(it.b)
            is Either.Left -> it
        }
    }

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
