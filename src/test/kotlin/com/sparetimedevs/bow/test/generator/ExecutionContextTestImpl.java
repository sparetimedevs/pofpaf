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

package com.sparetimedevs.bow.test.generator;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.TraceContext;

import java.util.logging.Logger;

/**
 * This class is derived from:
 * https://github.com/Azure/azure-functions-java-worker/blob/dev/src/main/java/com/microsoft/azure/functions/worker/binding/ExecutionContextDataSource.java
 */
final class ExecutionContextTestImpl implements ExecutionContext {

	private final String invocationId;
	private final TraceContext traceContext;
	private final Logger logger;
	private final String functionName;

	ExecutionContextTestImpl(String invocationId, TraceContext traceContext, Logger logger, String functionName) {
		this.invocationId = invocationId;
		this.traceContext = traceContext;
		this.logger = logger;
		this.functionName = functionName;
	}

	@Override
	public String getInvocationId() {
		return this.invocationId;
	}

	@Override
	public Logger getLogger() {
		return this.logger;
	}

	@Override
	public TraceContext getTraceContext() {
		return traceContext;
	}

	@Override
	public String getFunctionName() {
		return functionName;
	}
}
