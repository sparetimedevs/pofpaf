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

import com.microsoft.azure.functions.TraceContext;

import java.util.Map;

/**
 * This class is derived from:
 * https://github.com/Azure/azure-functions-java-worker/blob/dev/src/main/java/com/microsoft/azure/functions/worker/binding/ExecutionTraceContext.java
 */
final class ExecutionTraceContextTestImpl implements TraceContext {

	private final String traceParent;
	private final String traceState;
	private final Map<String, String> attributes;

	ExecutionTraceContextTestImpl(String traceParent, String traceState, Map<String, String> attributes) {
		this.traceParent = traceParent;
		this.traceState = traceState;
		this.attributes = attributes;
	}

	@Override
	public String getTraceparent() {
		return traceParent;
	}

	@Override
	public String getTracestate() {
		return traceState;
	}

	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}
}
