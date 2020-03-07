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

package com.sparetimedevs.bow.http

import arrow.fx.IO
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.bow.test.ALL_ASSERTIONS_ARE_POSITIVE
import com.sparetimedevs.bow.test.generator.ExecutionContextGenerator
import com.sparetimedevs.bow.test.generator.HttpRequestMessageGenerator
import com.sparetimedevs.bow.test.generator.io
import com.sparetimedevs.bow.test.generator.ioJustAny
import com.sparetimedevs.bow.test.generator.ioRaiseAnyError
import com.sparetimedevs.bow.test.generator.ioRaiseAnyException
import io.kotlintest.matchers.shouldBeInRange
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class HttpHandlerKtTest : StringSpec({

    "Should yield an HttpResponseMessage when default handlers are used." {
        forAll(Gen.io(), HttpRequestMessageGenerator(), ExecutionContextGenerator()) { domainLogic: IO<Any, Any>,
                                                                                       request: HttpRequestMessage<String?>,
                                                                                       context: ExecutionContext ->
            val response =
                handleHttp(
                    request = request,
                    context = context,
                    domainLogic = domainLogic
                )

            response.shouldBeInstanceOf<HttpResponseMessage>()
            response.statusCode shouldBeInRange IntRange(100, 599)
            ALL_ASSERTIONS_ARE_POSITIVE
        }
    }

    "Should yield an HttpResponseMessage when an exception is thrown in the handleSuccess supplied function." {
        forAll(Gen.ioJustAny(), HttpRequestMessageGenerator(), ExecutionContextGenerator()) { domainLogic: IO<Any, Any>,
                                                                                              request: HttpRequestMessage<String?>,
                                                                                              context: ExecutionContext ->
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
            ALL_ASSERTIONS_ARE_POSITIVE
        }
    }

    "Should yield an HttpResponseMessage when an exception is thrown in the handleDomainError supplied function." {
        forAll(Gen.ioRaiseAnyError(), HttpRequestMessageGenerator(), ExecutionContextGenerator()) { ioRaiseAnyError: IO<Any, Nothing>,
                                                                                                    request: HttpRequestMessage<String?>,
                                                                                                    context: ExecutionContext ->
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
            ALL_ASSERTIONS_ARE_POSITIVE
        }
    }

    "Should yield an HttpResponseMessage when an exception is thrown in the handleSystemFailure supplied function." {
        forAll(Gen.ioRaiseAnyException(), HttpRequestMessageGenerator(), ExecutionContextGenerator()) { ioRaiseAnyException: IO<Any, Nothing>,
                                                                                                        request: HttpRequestMessage<String?>,
                                                                                                        context: ExecutionContext ->
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
            ALL_ASSERTIONS_ARE_POSITIVE
        }
    }
})

private val exception = RuntimeException("An Exception is thrown while handling the result of the domain logic.")

@Suppress("UNUSED_PARAMETER")
private fun <T> throwException(request: HttpRequestMessage<String?>, context: ExecutionContext, t: T): IO<Nothing, HttpResponseMessage> =
    IO { throw exception }
