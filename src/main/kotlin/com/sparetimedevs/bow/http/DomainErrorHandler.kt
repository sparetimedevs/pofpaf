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
import java.util.Optional

@Suppress("UNUSED_PARAMETER")
fun <E> handleDomainErrorWithDefaultHandler(request: HttpRequestMessage<Optional<String>>, context: ExecutionContext, e: E): IO<Nothing, HttpResponseMessage> =
        createResponse(request, e)

fun <E> createResponse(request: HttpRequestMessage<Optional<String>>, e: E): IO<Nothing, HttpResponseMessage> =
        IO {
            request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse("$ERROR_MESSAGE_PREFIX $e"))
                    .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                    .build()
        }
