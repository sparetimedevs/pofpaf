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

package com.sparetimedevs.pofpaf.test.implementation.azurefunctions.timer

import arrow.core.Either
import arrow.core.right

@Suppress("UNUSED_PARAMETER")
suspend fun <E> handleDomainErrorWithDefaultHandler(timerInfo: String, log: suspend (e: E) -> Either<Throwable, Unit>, e: E): Either<Throwable, Unit> =
    Unit.right()
