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

package com.sparetimedevs.pofpaf.log

import com.microsoft.azure.functions.ExecutionContext
import com.sparetimedevs.pofpaf.test.generator.executionContextArb
import com.sparetimedevs.pofpaf.test.generator.logLevel
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import java.util.logging.Level

class LoggerKtTest : StringSpec({
    
    val contextMock = mockk<ExecutionContext>()
    every { contextMock.logger.log(any(), any<String>()) } throws exception
    
    "Should not yield anything other than Unit when run unsafe." {
        checkAll(Arb.executionContextArb(), Exhaustive.logLevel(), Arb.string(maxSize = 10_000)) { context: ExecutionContext,
                                                                                                   level: Level,
                                                                                                   message: String ->
            
            val result = log(context, level, message)
            
            result.shouldBeRight()
        }
    }
    
    "Should not throw exception when run unsafe." {
        checkAll(Exhaustive.logLevel(), Arb.string(maxSize = 100_000)) { level: Level, message: String ->
            
            val result = log(contextMock, level, message)
            
            result.shouldBeLeft(exception)
        }
    }
})

private val exception = RuntimeException("Break stuff")
