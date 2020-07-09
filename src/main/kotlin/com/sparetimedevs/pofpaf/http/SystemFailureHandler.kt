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

import arrow.core.Either
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.pofpaf.log.log
import com.sparetimedevs.pofpaf.util.flatMap
import java.util.logging.Level

suspend fun handleSystemFailureWithDefaultHandler(
    request: HttpRequestMessage<out Any?>,
    context: ExecutionContext,
    throwable: Throwable
): Either<Throwable, HttpResponseMessage> =
    log(context, Level.SEVERE, "$THROWABLE_MESSAGE_PREFIX $throwable. ${throwable.message}")
        .flatMap {
            createResponse(request, throwable)
        }

suspend fun createResponse(request: HttpRequestMessage<out Any?>, throwable: Throwable): Either<Throwable, HttpResponseMessage> =
    Either.catch {
        request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("$THROWABLE_MESSAGE_PREFIX $throwable"))
            .header(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
            .build()
    }
