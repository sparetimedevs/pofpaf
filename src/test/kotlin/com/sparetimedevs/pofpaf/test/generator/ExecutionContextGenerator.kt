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

package com.sparetimedevs.pofpaf.test.generator

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.TraceContext
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.create
import io.kotest.property.arbitrary.string
import java.util.logging.Logger

private fun Arb.Companion.traceContextArb(): Arb<TraceContext> =
    bind(
        string(), string(), mapOfStringAndStringGenerator()
    ) { traceParent, traceState, attributes ->
        ExecutionTraceContextTestImpl(traceParent, traceState, attributes)
    }

fun Arb.Companion.executionContextArb(): Arb<ExecutionContext> =
    bind(
        string(), traceContextArb(), create { Logger.getGlobal() }, string()
    ) { invocationId, traceContext, logger, functionName ->
        ExecutionContextTestImpl(invocationId, traceContext, logger, functionName)
    }
