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

package com.sparetimedevs.bow.timer

import arrow.fx.IO
import com.microsoft.azure.functions.ExecutionContext
import com.sparetimedevs.bow.test.ALL_ASSERTIONS_ARE_POSITIVE
import com.sparetimedevs.bow.test.generator.ExecutionContextGenerator
import com.sparetimedevs.bow.test.generator.ioOfAnyAndUnit
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

class TimerHandlerKtTest : StringSpec({

    "Should yield a Unit when default handlers are used." {
        forAll(
            Gen.string(maxSize = 500),
            ExecutionContextGenerator(),
            Gen.ioOfAnyAndUnit()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: IO<Any, Unit> ->

            val response =
                handleTimer(
                    timerInfo = timerInfo,
                    context = context,
                    domainLogic = domainLogic
                )

            response.shouldBeInstanceOf<Unit>()
            ALL_ASSERTIONS_ARE_POSITIVE
        }
    }

    "Should yield a Unit when an exception is thrown in the handleSuccess supplied function." {
        forAll(
            Gen.string(maxSize = 500),
            ExecutionContextGenerator(),
            Gen.ioOfAnyAndUnit()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: IO<Any, Unit> ->

            val response =
                handleTimer(
                    timerInfo = timerInfo,
                    context = context,
                    domainLogic = domainLogic,
                    handleSuccess = ::throwException
                )

            response.shouldBeInstanceOf<Unit>()
            ALL_ASSERTIONS_ARE_POSITIVE
        }
    }

    "Should yield a Unit when an exception is thrown in the handleDomainError supplied function." {
        forAll(
            Gen.string(maxSize = 500),
            ExecutionContextGenerator(),
            Gen.ioOfAnyAndUnit()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: IO<Any, Unit> ->

            val response =
                handleTimer(
                    timerInfo = timerInfo,
                    context = context,
                    domainLogic = domainLogic,
                    handleDomainError = ::throwException
                )

            response.shouldBeInstanceOf<Unit>()
            ALL_ASSERTIONS_ARE_POSITIVE
        }
    }

    "Should yield a Unit when an exception is thrown in the handleSystemFailure supplied function." {
        forAll(
            Gen.string(maxSize = 500),
            ExecutionContextGenerator(),
            Gen.ioOfAnyAndUnit()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: IO<Any, Unit> ->

            val response =
                handleTimer(
                    timerInfo = timerInfo,
                    context = context,
                    domainLogic = domainLogic,
                    handleSystemFailure = ::throwException
                )

            response.shouldBeInstanceOf<Unit>()
            ALL_ASSERTIONS_ARE_POSITIVE
        }
    }
})

private val exception = RuntimeException("An Exception is thrown while handling the result of the domain logic.")

@Suppress("UNUSED_PARAMETER")
private fun throwException(timerInfo: String, context: ExecutionContext): IO<Nothing, Unit> =
    IO { throw exception }

@Suppress("UNUSED_PARAMETER")
private fun <T> throwException(timerInfo: String, context: ExecutionContext, t: T): IO<Nothing, Unit> =
    IO { throw exception }
