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

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

import com.evinceframework.web.messaging.exceptions.UserMessageTransform;

/**
 * Transforms an exception to a single user message getting the message and description from 
 * the provided {@link MessageSourceAccessor}.
 * 
 * @author Craig Swing
 *
 * @param <T>
 * @see UserMessageTransform
 * @see SingleMessageTransform
 * @see MessageSourceAccessor
 */
public class StaticMessageTransform<T extends Throwable> extends SingleMessageTransform<T> {

	private String errorCode;
	
	private MessageSourceAccessor messageSourceAccessor;
	
	private String messageKey;
	
	private String descriptionKey;
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public MessageSourceAccessor getMessageSourceAccessor() {
		return messageSourceAccessor;
	}

	public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
		this.messageSourceAccessor = messageSourceAccessor;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getDescriptionKey() {
		return descriptionKey;
	}

	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	@Override
	protected String getErrorCode(T throwable) {
		
		if (errorCode == null)
			return super.getErrorCode(throwable);
		
		return errorCode;
	}

	@Override
	protected String getMessage(T throwable) {
		
		String message = null;
		if(StringUtils.hasText(messageKey)) {
			message = messageSourceAccessor.getMessage(messageKey);
		}
		
		if (StringUtils.hasText(message))
			return message;
		
		return super.getMessage(throwable);
	}

	@Override
	protected String getDescription(T throwable) {
		
		String description = null;
		if(StringUtils.hasText(descriptionKey)) {
			description = messageSourceAccessor.getMessage(descriptionKey);
		}
		String baseDescription = super.getDescription(throwable);
		
		if (StringUtils.hasText(description) && StringUtils.hasText(baseDescription)) {
			return String.format("%s\n\n%s", description, super.getDescription(throwable));
		}
		
		if (StringUtils.hasText(baseDescription)) {
			return description;
		}
		
		if (StringUtils.hasText(baseDescription)) {
			return description;
		}
		
		return "";
	}

}