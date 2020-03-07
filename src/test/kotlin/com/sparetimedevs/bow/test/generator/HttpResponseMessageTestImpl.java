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

import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.HttpStatusType;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is derived from:
 * https://github.com/Azure/azure-functions-java-worker/blob/dev/src/main/java/com/microsoft/azure/functions/worker/binding/RpcHttpDataTarget.java
 */
final class HttpResponseMessageTestImpl implements HttpResponseMessage, HttpResponseMessage.Builder {

	private int httpStatusCode;
	private HttpStatusType httpStatus;
	private Object body;
	private Map<String, String> headers;

	HttpResponseMessageTestImpl() {
		this.headers = new HashMap<>();
		this.httpStatus = HttpStatus.OK;
		this.httpStatusCode = HttpStatus.OK.value();
	}

	@Override
	public HttpStatusType getStatus() {
		return httpStatus;
	}

	@Override
	public int getStatusCode() {
		return httpStatusCode;
	}

	@Override
	public String getHeader(String key) {
		return headers.get(key);
	}

	@Override
	public Object getBody() {
		return this.body;
	}

	public Builder status(HttpStatus status) {
		this.httpStatusCode = status.value();
		this.httpStatus = status;
		return this;
	}

	@Override
	public Builder status(HttpStatusType httpStatusType) {
		this.httpStatusCode = httpStatusType.value();
		this.httpStatus = httpStatusType;
		return this;
	}

	@Override
	public Builder header(String key, String value) {
		this.headers.put(key, value);
		return this;
	}

	@Override
	public Builder body(Object body) {
		this.body = body;
		return this;
	}

	@Override
	public HttpResponseMessage build() {
		return this;
	}
}
