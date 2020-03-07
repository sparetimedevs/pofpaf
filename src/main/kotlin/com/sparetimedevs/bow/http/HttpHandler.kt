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
import arrow.fx.handleErrorWith
import arrow.fx.redeemWith
import arrow.fx.unsafeRunSync
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import java.util.Optional

fun <E, A> handleHttp(
        request: HttpRequestMessage<Optional<String>>,
        context: ExecutionContext,
        domainLogic: IO<E, A>,
        handleSuccess: (request: HttpRequestMessage<Optional<String>>, context: ExecutionContext, a: A) -> IO<Nothing, HttpResponseMessage> =
                ::handleSuccessWithDefaultHandler,
        handleDomainError: (request: HttpRequestMessage<Optional<String>>, context: ExecutionContext, e: E) -> IO<Nothing, HttpResponseMessage> =
                ::handleDomainErrorWithDefaultHandler,
        handleSystemFailure: (request: HttpRequestMessage<Optional<String>>, context: ExecutionContext, throwable: Throwable) -> IO<Nothing, HttpResponseMessage> =
                ::handleSystemFailureWithDefaultHandler
): HttpResponseMessage =
        domainLogic
                .redeemWith(
                        { throwable: Throwable ->
                            handleSystemFailure(request, context, throwable)
                        },
                        { e: E ->
                            handleDomainError(request, context, e)
                        },
                        { a: A ->
                            handleSuccess(request, context, a)
                        }
                )
                .handleErrorWith { handleSystemFailureWithDefaultHandler(request, context, it)  }
                .unsafeRunSync()
