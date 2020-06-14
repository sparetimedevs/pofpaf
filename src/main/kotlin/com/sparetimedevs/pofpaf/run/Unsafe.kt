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

package com.sparetimedevs.pofpaf.run

import arrow.core.Either
import java.util.concurrent.locks.AbstractQueuedSynchronizer
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/**
 * Implementation taken from "arrow.fx.coroutines.Platform.kt".
 */
internal fun <A> (suspend () -> A).unsafeRunSync(): A {
    val latch = OneShotLatch()
    var ref: Either<Throwable, A>? = null
    this.startCoroutine(Continuation(EmptyCoroutineContext) { a ->
        ref = a.fold({ aa -> Either.Right(aa) }, { t -> Either.Left(t) })
        latch.releaseShared(1)
    })
    
    latch.acquireSharedInterruptibly(1)
    
    return when (val either = ref) {
        is Either.Left -> throw either.a
        is Either.Right -> either.b
        null -> throw RuntimeException("Suspend execution should yield a valid result")
    }
}

/**
 * Implementation taken from "arrow.fx.coroutines.Platform.kt".
 */
private class OneShotLatch : AbstractQueuedSynchronizer() {
    override fun tryAcquireShared(ignored: Int): Int =
        if (state != 0) {
            1
        } else {
            -1
        }
    
    override fun tryReleaseShared(ignore: Int): Boolean {
        state = 1
        return true
    }
}
