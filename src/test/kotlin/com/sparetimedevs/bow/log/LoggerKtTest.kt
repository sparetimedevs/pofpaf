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

import arrow.fx.unsafeRunSync
import com.microsoft.azure.functions.ExecutionContext
import com.sparetimedevs.bow.test.ALL_ASSERTIONS_ARE_POSITIVE
import com.sparetimedevs.bow.test.generator.ExecutionContextGenerator
import com.sparetimedevs.bow.test.generator.logLevel
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import java.util.logging.Level

class LoggerKtTest : StringSpec({
    
    val contextMock = mockk<ExecutionContext>()
    every { contextMock.logger.log(any(), any<String>()) } throws RuntimeException("Break stuff")
    
    "Should not yield anything other than Unit when run unsafe." {
        forAll(ExecutionContextGenerator(), Gen.logLevel(), Gen.string(maxSize = 10_000)) { context: ExecutionContext,
                                                                                            level: Level,
                                                                                            message: String ->
            
            val result = log(context, level, message).unsafeRunSync()
            
            result.shouldBeInstanceOf<Unit>()
            ALL_ASSERTIONS_ARE_POSITIVE
        }
    }
    
    "Should not throw exception when run unsafe." {
        forAll(Gen.logLevel(), Gen.string(maxSize = 100_000)) { level: Level, message: String ->
            
            val result = log(contextMock, level, message).unsafeRunSync()
            
            result.shouldBeInstanceOf<Unit>()
            ALL_ASSERTIONS_ARE_POSITIVE
        }
    }
})
