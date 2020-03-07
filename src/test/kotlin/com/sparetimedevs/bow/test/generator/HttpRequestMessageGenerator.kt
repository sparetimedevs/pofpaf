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

package com.sparetimedevs.bow.test.generator

import com.microsoft.azure.functions.HttpRequestMessage
import io.kotlintest.properties.Gen

internal class HttpRequestMessageGenerator : Gen<HttpRequestMessage<String?>> {
    override fun constants(): List<HttpRequestMessage<String?>> = emptyList()
    override fun random(): Sequence<HttpRequestMessage<String?>> = generateSequence {
        HttpRequestMessageTestImpl(
                Gen.uri().random().first(),
                Gen.httpMethod().random().first(),
                Gen.mapOfStringAndStringGenerator().random().first(),
                Gen.mapOfStringAndStringGenerator().random().first(),
                Gen.string().orNull().random().first()
        )
    }
}
