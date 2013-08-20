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
package com.evinceframework.web.mvc;

import org.springframework.ui.ModelMap;

import com.evinceframework.web.dojo.mvc.model.ViewModelUtils;
import com.evinceframework.web.messaging.UserMessage;
import com.evinceframework.web.messaging.UserMessageType;

public abstract class ControllerSupport {

	protected static final String URL_DATE_FORMAT = "yyyyMMdd";
	
	protected static final String DEFAULT_WEB_CODE = "WEB-000";
	
	protected static final ViewModelUtils modelUtil = new ViewModelUtils();

	protected void addErrorMessage(ModelMap model, String message) {
		addUserMessage(model, new UserMessage(UserMessageType.ERROR, DEFAULT_WEB_CODE, message, ""));
	}
	
	protected void addWarningMessage(ModelMap model, String message) {
		addUserMessage(model, new UserMessage(UserMessageType.WARNING, DEFAULT_WEB_CODE, message, ""));
	}

	protected void addInformationalMessage(ModelMap model, String message) {
		addUserMessage(model, new UserMessage(UserMessageType.INFORMATIONAL, DEFAULT_WEB_CODE, message, ""));
	}
	
	protected void addUserMessage(ModelMap model, UserMessageType type, String message) {
		addUserMessage(model, new UserMessage(type, DEFAULT_WEB_CODE, message, ""));
	}
	
	protected void addUserMessage(ModelMap model, UserMessage userMessage) {
		modelUtil.addToViewModel(model, userMessage);
	}
}
