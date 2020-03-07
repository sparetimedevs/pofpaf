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

import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage.Builder;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.HttpStatusType;

import java.net.URI;
import java.util.Map;

/**
 * This class is derived from:
 * https://github.com/Azure/azure-functions-java-worker/blob/dev/src/main/java/com/microsoft/azure/functions/worker/binding/RpcHttpRequestDataSource.java
 */
final class HttpRequestMessageTestImpl<T> implements HttpRequestMessage<T> {

	private URI uri;
	private HttpMethod httpMethod;
	private Map<String, String> headers;
	private Map<String, String> queryParameters;
	private T body;

	HttpRequestMessageTestImpl(
			URI uri,
			HttpMethod httpMethod,
			Map<String, String> headers,
			Map<String, String> queryParameters,
			T body
	) {
		this.uri = uri;
		this.httpMethod = httpMethod;
		this.headers = headers;
		this.queryParameters = queryParameters;
		this.body = body;
	}

	@Override
	public URI getUri() {
		return uri;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}

	@Override
	public T getBody() {
		return body;
	}

	@Override
	public Builder createResponseBuilder(HttpStatusType status) {
		return new HttpResponseMessageTestImpl().status(status);
	}

	@Override
	public Builder createResponseBuilder(HttpStatus status) {
		return new HttpResponseMessageTestImpl().status(status);
	}
}
