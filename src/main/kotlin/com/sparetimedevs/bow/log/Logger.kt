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

package com.sparetimedevs.bow.log

import arrow.fx.IO
import arrow.fx.handleError
import com.microsoft.azure.functions.ExecutionContext
import java.util.logging.Level

fun log(context: ExecutionContext, level: Level, message: String): IO<Nothing, Unit> =
        IO {
            context.logger.log(level, message)
        }.handleError { /* Do nothing in case of logging resulting in an exception being thrown. */ }
