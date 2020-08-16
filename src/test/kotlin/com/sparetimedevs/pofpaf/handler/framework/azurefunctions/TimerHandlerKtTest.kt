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
import com.sparetimedevs.pofpaf.log.Level
import com.sparetimedevs.pofpaf.test.generator.executionContextArb
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsAnyLeft
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsEitherAnyOrUnitOrThrows
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsUnitRight
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatThrows
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.log.log
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.timer.handleDomainErrorWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.timer.handleSuccessWithDefaultHandler
import com.sparetimedevs.pofpaf.test.implementation.azurefunctions.timer.handleSystemFailureWithDefaultHandler
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
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            val result =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> throwException(timerInfo, log, a) },
                    handleDomainError = { e -> handleDomainErrorWithDefaultHandler(timerInfo, log, e) },
                    handleSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, log, throwable) },
                    handleHandlerFailure = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, log, throwable) },
                    log = log
                )
            
            result.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should yield a Unit when an exception is thrown in the handleSuccess supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsUnitRight()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            val result =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { _ -> handleSuccessWithDefaultHandler(timerInfo, log) },
                    handleDomainError = { e -> handleDomainErrorWithDefaultHandler(timerInfo, log, e) },
                    handleSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, log, throwable) },
                    handleHandlerFailure = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, log, throwable) },
                    log = log
                )
            
            result.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should yield a Unit when an exception is thrown in the handleDomainError supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsAnyLeft() as Arb<suspend () -> Either<Any, Unit>>
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            val result =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { _ -> handleSuccessWithDefaultHandler(timerInfo, log) },
                    handleDomainError = { e -> throwException(timerInfo, log, e) },
                    handleSystemFailure = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, log, throwable) },
                    handleHandlerFailure = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, log, throwable) },
                    log = log
                )
            
            result.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should yield a Unit when an exception is thrown in the handleSystemFailure supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatThrows() as Arb<suspend () -> Either<Any, Unit>>
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            val result =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { _ -> handleSuccessWithDefaultHandler(timerInfo, log) },
                    handleDomainError = { e -> handleDomainErrorWithDefaultHandler(timerInfo, log, e) },
                    handleSystemFailure = { throwable -> throwException(timerInfo, log, throwable) },
                    handleHandlerFailure = { throwable -> handleSystemFailureWithDefaultHandler(timerInfo, log, throwable) },
                    log = log
                )
            
            result.shouldBeInstanceOf<Unit>()
        }
    }
    
    "Should throw a Throwable when any exception is thrown in the handleHandlerFailure supplied function." {
        checkAll(
            Arb.string(maxSize = 500),
            Arb.executionContextArb(),
            Arb.suspendFunThatReturnsEitherAnyOrUnitOrThrows()
        ) { timerInfo: String,
            context: ExecutionContext,
            domainLogic: suspend () -> Either<Any, Unit> ->
            
            val log: suspend (level: Level, message: String) -> Either<Throwable, Unit> = { level, message -> log(context, level, message) }
            
            shouldThrow<Throwable> {
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { throwable -> throwException(timerInfo, log, throwable) },
                    handleDomainError = { throwable -> throwException(timerInfo, log, throwable) },
                    handleSystemFailure = { throwable -> throwException(timerInfo, log, throwable) },
                    handleHandlerFailure = { throwable -> throwException(timerInfo, log, throwable) },
                    log = log
                )
            }
        }
    }
})

private val exception = RuntimeException("An Exception is thrown while handling the result of the domain logic.")

@Suppress("UNUSED_PARAMETER")
private suspend fun throwException(timerInfo: String, log: suspend (level: Level, message: String) -> Either<Throwable, Unit>): Either<Throwable, Unit> =
    throw exception

@Suppress("UNUSED_PARAMETER")
private suspend fun <T> throwException(timerInfo: String, log: suspend (level: Level, message: String) -> Either<Throwable, Unit>, t: T): Either<Throwable, Unit> =
    throw exception
