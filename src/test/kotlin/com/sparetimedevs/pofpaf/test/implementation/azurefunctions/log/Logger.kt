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

package com.sparetimedevs.pofpaf.test.implementation.azurefunctions.log

import arrow.core.Either
import com.microsoft.azure.functions.ExecutionContext
import com.sparetimedevs.pofpaf.test.implementation.general.log.Level

suspend fun log(context: ExecutionContext, level: Level, message: String): Either<Throwable, Unit> =
    Either.catch {
        context.logger.log(getAzureFunctionsCompatible(level), message)
    }

fun getAzureFunctionsCompatible(level: Level): java.util.logging.Level =
    when (level) {
        Level.TRACE, Level.DEBUG, Level.INFO -> {
            java.util.logging.Level.INFO
        }
        Level.WARN -> {
            java.util.logging.Level.WARNING
        }
        Level.ERROR -> {
            java.util.logging.Level.SEVERE
        }
    }
