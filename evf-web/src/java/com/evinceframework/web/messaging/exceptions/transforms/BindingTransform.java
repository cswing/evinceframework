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
package com.evinceframework.web.messaging.exceptions.transforms;


import java.util.Locale;
import java.util.Set;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.evinceframework.web.messaging.UserMessage;
import com.evinceframework.web.messaging.UserMessageType;

public class BindingTransform extends AbstractUserMessageTransform {

	private static final String ERROR_CODE = "BIND-000";
	
	private MessageSource messageSource;
	
	public BindingTransform(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	protected void onTransform(Throwable t, Set<UserMessage> messages) {
		
		Locale locale = LocaleContextHolder.getLocale();
		
		BindException be = (BindException)t;
		
		for(ObjectError ge : be.getGlobalErrors()) {
			messages.add(new UserMessage(UserMessageType.ERROR, ERROR_CODE, getMessage(ge, locale), ""));
		}
		
		for(FieldError fe : be.getFieldErrors()) {
			messages.add(new UserMessage(UserMessageType.ERROR, ERROR_CODE, getMessage(fe, locale), "", fe.getField()));
		}
	}
	
	protected String getMessage(ObjectError oe, Locale locale) {
		if(messageSource == null)
			return oe.getCode();
		
		return messageSource.getMessage(oe, locale);
	}

}
