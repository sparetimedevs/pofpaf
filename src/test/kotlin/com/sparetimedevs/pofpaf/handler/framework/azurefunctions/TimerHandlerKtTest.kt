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

package com.sparetimedevs.pofpaf.handler.framework.azurefunctions

import arrow.core.Either
import com.microsoft.azure.functions.ExecutionContext
import com.sparetimedevs.pofpaf.handler.handleBlocking
import com.sparetimedevs.pofpaf.test.generator.executionContextArb
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsAnyLeft
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsEitherAnyOrUnitOrThrows
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsUnitRight
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatThrows
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.log.log
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.timer.handleDomainErrorWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.timer.handleSuccessWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.timer.handleSystemFailureWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.general.log.Level
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class TimerHandlerKtTest : StringSpec({
    
    "Should yield a Unit when deterministic functions are used as handlers." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsEitherAnyOrUnitOrThrows()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val result =
                handleBlocking(
                    logic = domainLogic,
                    ifSuccess = { _ -> handleSuccessWithDefaultHandler(timerInfo, { log(context, Level.INFO, "This did happen.") }) },
                    ifDomainError = { e -> handleDomainErrorWithDefaultHandler(timerInfo, { e: Any -> log(context, Level.WARN, "This is e: $e") }, e) },
                    ifSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    ifHandlingCaseThrows = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    ifUnrecoverableState = { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }
                )
            
            result.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should yield a Unit when an exception is thrown in the ifSuccess supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsUnitRight()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val result =
                handleBlocking(
                    logic = domainLogic,
                    ifSuccess = { _ -> throwException(timerInfo, { log(context, Level.INFO, "This did happen.") }) },
                    ifDomainError = { e -> handleDomainErrorWithDefaultHandler(timerInfo, { e: Any -> log(context, Level.WARN, "This is e: $e") }, e) },
                    ifSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    ifHandlingCaseThrows = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    ifUnrecoverableState = { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }
                )
            
            result.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should yield a Unit when an exception is thrown in the ifDomainError supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsAnyLeft() as Arb<suspend () -> Either<Any, Unit>>
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val result =
                handleBlocking(
                    logic = domainLogic,
                    ifSuccess = { _ -> handleSuccessWithDefaultHandler(timerInfo, { log(context, Level.INFO, "This did happen.") }) },
                    ifDomainError = { e -> throwException(timerInfo, { e: Any -> log(context, Level.WARN, "This is e: $e") }, e) },
                    ifSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    ifHandlingCaseThrows = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    ifUnrecoverableState = { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }
                )
            
            result.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should yield a Unit when an exception is thrown in the ifSystemFailure supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatThrows() as Arb<suspend () -> Either<Any, Unit>>
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val result =
                handleBlocking(
                    logic = domainLogic,
                    ifSuccess = { _ -> handleSuccessWithDefaultHandler(timerInfo, { log(context, Level.INFO, "This did happen.") }) },
                    ifDomainError = { e -> handleDomainErrorWithDefaultHandler(timerInfo, { e: Any -> log(context, Level.WARN, "This is e: $e") }, e) },
                    ifSystemFailure = { throwable -> throwException(timerInfo, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    ifHandlingCaseThrows = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    ifUnrecoverableState = { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }
                )
            
            result.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should throw a Throwable when any exception is thrown in the ifHandlingCaseThrows supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsEitherAnyOrUnitOrThrows()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            shouldThrow<Throwable> {
                handleBlocking(
                    logic = domainLogic,
                    ifSuccess = { _ -> throwException(timerInfo, { log(context, Level.INFO, "This did happen.") }) },
                    ifDomainError = { throwable -> throwException(timerInfo, { e: Any -> log(context, Level.WARN, "This is e: $e") }, throwable) },
                    ifSystemFailure = { throwable -> throwException(timerInfo, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    ifHandlingCaseThrows = { throwable -> throwException(timerInfo, { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }, throwable) },
                    ifUnrecoverableState = { throwable: Throwable -> log(context, Level.ERROR, "Log the throwable: $throwable.") }
                )
            }
        }
    }
})

private val exception = RuntimeException("An Exception is thrown while handling the result of the domain logic.")

@Suppress("UNUSED_PARAMETER")
private suspend fun throwException(timerInfo: String, log: suspend () -> Either<Throwable, Unit>): Either<Throwable, Unit> =
    throw exception

@Suppress("UNUSED_PARAMETER")
private suspend fun <T> throwException(timerInfo: String, log: suspend (throwable: Throwable) -> Either<Throwable, Unit>, t: T): Either<Throwable, Unit> =
    throw exception
