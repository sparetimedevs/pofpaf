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
import com.sparetimedevs.pofpaf.log.Level
import com.sparetimedevs.pofpaf.test.generator.any
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsAnyLeft
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsAnyRight
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatReturnsEitherAnyOrAnyOrThrows
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatThrows
import com.sparetimedevs.pofpaf.test.generator.suspendFunThatThrowsFatalThrowable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll

class HandlerKtTest : StringSpec({
    
    "handle should yield a result when deterministic functions are used as handlers" {
        checkAll(
            Arb.suspendFunThatReturnsEitherAnyOrAnyOrThrows(),
            Arb.any()
        ) { domainLogic: suspend () -> Either<Any, Any>,
            returnObject: Any ->
            
            val result =
                handle(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleWithPureFunction(a, returnObject) },
                    handleDomainError = { e -> handleWithPureFunction(e, returnObject) },
                    handleSystemFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    handleHandlerFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    log = ::log
                )
            
            result shouldBe returnObject
        }
    }
    
    "handle should throw a Throwable when a fatal Throwable is thrown" {
        checkAll(
            Arb.suspendFunThatThrowsFatalThrowable(),
            Arb.any()
        ) { domainLogic: suspend () -> Either<Any, Any>,
            returnObject: Any ->
            
            shouldThrow<Throwable> {
                handle(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleWithPureFunction(a, returnObject) },
                    handleDomainError = { e -> handleWithPureFunction(e, returnObject) },
                    handleSystemFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    handleHandlerFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    log = ::log
                )
            }
        }
    }
    
    "handle should yield a result when an exception is thrown in the handleSuccess supplied function" {
        checkAll(
            Arb.suspendFunThatReturnsAnyRight(),
            Arb.any()
        ) { domainLogic: suspend () -> Either<Any, Any>,
            returnObject: Any ->
            
            val result =
                handle(
                    domainLogic = domainLogic,
                    handleSuccess = ::throwException,
                    handleDomainError = { e -> handleWithPureFunction(e, returnObject) },
                    handleSystemFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    handleHandlerFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    log = ::log
                )
            
            result shouldBe returnObject
        }
    }
    
    "handle should yield a result when an exception is thrown in the handleDomainError supplied function" {
        checkAll(
            Arb.suspendFunThatReturnsAnyLeft(),
            Arb.any()
        ) { domainLogic: suspend () -> Either<Any, Any>,
            returnObject: Any ->
            
            val result =
                handle(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleWithPureFunction(a, returnObject) },
                    handleDomainError = ::throwException,
                    handleSystemFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    handleHandlerFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    log = ::log
                )
            
            result shouldBe returnObject
        }
    }
    
    "handle should yield a result when an exception is thrown in the handleSystemFailure supplied function" {
        checkAll(
            Arb.suspendFunThatThrows(),
            Arb.any()
        ) { domainLogic: suspend () -> Either<Any, Any>,
            returnObject: Any ->
            
            val result =
                handle(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleWithPureFunction(a, returnObject) },
                    handleDomainError = { e -> handleWithPureFunction(e, returnObject) },
                    handleSystemFailure = ::throwException,
                    handleHandlerFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    log = ::log
                )
            
            result shouldBe returnObject
        }
    }
    
    "handle should throw a Throwable when any exception is thrown in the handleHandlerFailure supplied function" {
        checkAll(
            Arb.suspendFunThatReturnsEitherAnyOrAnyOrThrows()
        ) { domainLogic: suspend () -> Either<Any, Any> ->
            
            shouldThrow<Throwable> {
                handle(
                    domainLogic = domainLogic,
                    handleSuccess = ::throwException,
                    handleDomainError = ::throwException,
                    handleSystemFailure = ::throwException,
                    handleHandlerFailure = ::throwException,
                    log = ::log
                )
            }
        }
    }
    
    "handleBlocking should yield a result when deterministic functions are used as handlers" {
        checkAll(
            Arb.suspendFunThatReturnsEitherAnyOrAnyOrThrows(),
            Arb.any()
        ) { domainLogic: suspend () -> Either<Any, Any>,
            returnObject: Any ->
            
            val result =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleWithPureFunction(a, returnObject) },
                    handleDomainError = { e -> handleWithPureFunction(e, returnObject) },
                    handleSystemFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    handleHandlerFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    log = ::log
                )
            
            result shouldBe returnObject
        }
    }
    
    "handleBlocking should throw a Throwable when a fatal Throwable is thrown" {
        checkAll(
            Arb.suspendFunThatThrowsFatalThrowable(),
            Arb.any()
        ) { domainLogic: suspend () -> Either<Any, Any>,
            returnObject: Any ->
            
            shouldThrow<Throwable> {
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleWithPureFunction(a, returnObject) },
                    handleDomainError = { e -> handleWithPureFunction(e, returnObject) },
                    handleSystemFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    handleHandlerFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    log = ::log
                )
            }
        }
    }
    
    "handleBlocking should yield a result when an exception is thrown in the handleSuccess supplied function" {
        checkAll(
            Arb.suspendFunThatReturnsAnyRight(),
            Arb.any()
        ) { domainLogic: suspend () -> Either<Any, Any>,
            returnObject: Any ->
            
            val result =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = ::throwException,
                    handleDomainError = { e -> handleWithPureFunction(e, returnObject) },
                    handleSystemFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    handleHandlerFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    log = ::log
                )
            
            result shouldBe returnObject
        }
    }
    
    "handleBlocking should yield a result when an exception is thrown in the handleDomainError supplied function" {
        checkAll(
            Arb.suspendFunThatReturnsAnyLeft(),
            Arb.any()
        ) { domainLogic: suspend () -> Either<Any, Any>,
            returnObject: Any ->
            
            val result =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleWithPureFunction(a, returnObject) },
                    handleDomainError = ::throwException,
                    handleSystemFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    handleHandlerFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    log = ::log
                )
            
            result shouldBe returnObject
        }
    }
    
    "handleBlocking should yield a result when an exception is thrown in the handleSystemFailure supplied function" {
        checkAll(
            Arb.suspendFunThatThrows(),
            Arb.any()
        ) { domainLogic: suspend () -> Either<Any, Any>,
            returnObject: Any ->
            
            val result =
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = { a -> handleWithPureFunction(a, returnObject) },
                    handleDomainError = { e -> handleWithPureFunction(e, returnObject) },
                    handleSystemFailure = ::throwException,
                    handleHandlerFailure = { throwable -> handleWithPureFunction(throwable, returnObject) },
                    log = ::log
                )
            
            result shouldBe returnObject
        }
    }
    
    "handleBlocking should throw a Throwable when any exception is thrown in the handleHandlerFailure supplied function" {
        checkAll(
            Arb.suspendFunThatReturnsEitherAnyOrAnyOrThrows()
        ) { domainLogic: suspend () -> Either<Any, Any> ->
            
            shouldThrow<Throwable> {
                handleBlocking(
                    domainLogic = domainLogic,
                    handleSuccess = ::throwException,
                    handleDomainError = ::throwException,
                    handleSystemFailure = ::throwException,
                    handleHandlerFailure = ::throwException,
                    log = ::log
                )
            }
        }
    }
})

@Suppress("UNUSED_PARAMETER")
suspend fun handleWithPureFunction(a: Any, b: Any): Either<Throwable, Any> =
    b.right()

@Suppress("UNUSED_PARAMETER")
suspend fun log(level: Level, message: String): Either<Throwable, Unit> =
    Unit.right()

@Suppress("UNUSED_PARAMETER")
private suspend fun <A> throwException(
    a: A
): Either<Throwable, Any> =
    throw RuntimeException("An Exception is thrown while handling the result of the domain logic.")
