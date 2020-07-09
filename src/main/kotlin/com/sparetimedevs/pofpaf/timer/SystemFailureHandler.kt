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
import com.sparetimedevs.pofpaf.http.THROWABLE_MESSAGE_PREFIX
import com.sparetimedevs.pofpaf.log.log
import java.util.logging.Level

@Suppress("UNUSED_PARAMETER")
suspend fun handleSystemFailureWithDefaultHandler(timerInfo: String, context: ExecutionContext, throwable: Throwable): Either<Throwable, Unit> =
    log(context, Level.SEVERE, "$THROWABLE_MESSAGE_PREFIX $throwable. ${throwable.message}")
