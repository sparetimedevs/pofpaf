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
import com.microsoft.azure.functions.ExecutionContext
import com.sparetimedevs.pofpaf.test.generator.executionContextArb
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsEitherAnyOrUnitOrThrows
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class TimerHandlerKtTest : StringSpec({
    
    "Should yield a Unit when default handlers are used." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsEitherAnyOrUnitOrThrows()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val response =
                handleTimer(
                    timerInfo = timerInfo,
                    context = context,
                    domainLogic = domainLogic
                )
            
            response.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should yield a Unit when an exception is thrown in the handleSuccess supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsEitherAnyOrUnitOrThrows()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val response =
                handleTimer(
                    timerInfo = timerInfo,
                    context = context,
                    domainLogic = domainLogic,
                    handleSuccess = ::throwException
                )
            
            response.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should yield a Unit when an exception is thrown in the handleDomainError supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsEitherAnyOrUnitOrThrows()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val response =
                handleTimer(
                    timerInfo = timerInfo,
                    context = context,
                    domainLogic = domainLogic,
                    handleDomainError = ::throwException
                )
            
            response.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should yield a Unit when an exception is thrown in the handleSystemFailure supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsEitherAnyOrUnitOrThrows()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val response =
                handleTimer(
                    timerInfo = timerInfo,
                    context = context,
                    domainLogic = domainLogic,
                    handleSystemFailure = ::throwException
                )
            
            response.shouldBeInstanceOf<Unit>()
        }
    }
})

private val exception = RuntimeException("An Exception is thrown while handling the result of the domain logic.")

@Suppress("UNUSED_PARAMETER")
private suspend fun throwException(timerInfo: String, context: ExecutionContext): Either<Throwable, Unit> =
    throw exception

@Suppress("UNUSED_PARAMETER")
private suspend fun <T> throwException(timerInfo: String, context: ExecutionContext, t: T): Either<Throwable, Unit> =
    throw exception
