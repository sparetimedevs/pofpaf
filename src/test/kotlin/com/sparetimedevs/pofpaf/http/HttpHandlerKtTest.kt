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

package com.sparetimedevs.pofpaf.http

import arrow.fx.IO
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.pofpaf.test.generator.executionContextArb
import com.sparetimedevs.pofpaf.test.generator.httpRequestMessageGenerator
import com.sparetimedevs.pofpaf.test.generator.ioJustAny
import com.sparetimedevs.pofpaf.test.generator.ioOfAnyAndAny
import com.sparetimedevs.pofpaf.test.generator.ioRaiseAnyError
import com.sparetimedevs.pofpaf.test.generator.ioRaiseAnyException
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.checkAll

class HttpHandlerKtTest : StringSpec({
    
    "Should yield an HttpResponseMessage when default handlers are used." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.ioOfAnyAndAny()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: IO<Any, Any> ->
            
            val response =
                handleHttp(
                    request = request,
                    context = context,
                    domainLogic = domainLogic
                )
            
            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBeInRange IntRange(100, 599)
        }
    }
    
    "Should yield an HttpResponseMessage when an exception is thrown in the handleSuccess supplied function." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.ioJustAny()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            domainLogic: IO<Any, Any> ->
            
            val response =
                handleHttp(
                    request = request,
                    context = context,
                    domainLogic = domainLogic,
                    handleSuccess = ::throwException
                )
            
            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
            response.body shouldBe ErrorResponse("$THROWABLE_MESSAGE_PREFIX $exception")
        }
    }
    
    "Should yield an HttpResponseMessage when an exception is thrown in the handleDomainError supplied function." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.ioRaiseAnyError()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            ioRaiseAnyError: IO<Any, Nothing> ->
            
            val response =
                handleHttp(
                    request = request,
                    context = context,
                    domainLogic = ioRaiseAnyError,
                    handleDomainError = ::throwException
                )
            
            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
            response.body shouldBe ErrorResponse("$THROWABLE_MESSAGE_PREFIX $exception")
        }
    }
    
    "Should yield an HttpResponseMessage when an exception is thrown in the handleSystemFailure supplied function." {
        checkAll(
            Arb.httpRequestMessageGenerator(),
            Arb.executionContextArb(),
            Arb.ioRaiseAnyException()
        ) { request: HttpRequestMessage<String?>,
            context: ExecutionContext,
            ioRaiseAnyException: IO<Any, Nothing> ->
            
            val response =
                handleHttp(
                    request = request,
                    context = context,
                    domainLogic = ioRaiseAnyException,
                    handleSystemFailure = ::throwException
                )
            
            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.getHeader(CONTENT_TYPE) shouldBe CONTENT_TYPE_APPLICATION_JSON
            response.body shouldBe ErrorResponse("$THROWABLE_MESSAGE_PREFIX $exception")
        }
    }
})

private val exception = RuntimeException("An Exception is thrown while handling the result of the domain logic.")

@Suppress("UNUSED_PARAMETER")
private fun <T> throwException(request: HttpRequestMessage<out Any?>, context: ExecutionContext, t: T): IO<Nothing, HttpResponseMessage> =
    IO { throw exception }
