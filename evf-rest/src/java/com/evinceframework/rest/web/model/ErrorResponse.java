/*
 * Copyright 2013 the original author or authors.
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
package com.evinceframework.rest.web.model;

import java.util.Arrays;
import java.util.List;

import com.evinceframework.rest.web.HrefBuilder;
import com.evinceframework.web.messaging.UserMessage;

public class ErrorResponse extends AbstractResponse {

	private List<UserMessage> messages;
	
	public ErrorResponse(HrefBuilder builder, UserMessage message) {
		this(builder, Arrays.asList(new UserMessage[]{message}));
	}
	
	public ErrorResponse(HrefBuilder builder, List<UserMessage> messages) {
		super(builder);
		this.messages = messages;
	}

	public List<UserMessage> getMessages() {
		return messages;
	}
}
