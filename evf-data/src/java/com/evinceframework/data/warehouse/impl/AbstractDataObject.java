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
package com.evinceframework.data.warehouse.impl;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

public abstract class AbstractDataObject {

	private final Log logger = LogFactory.getLog(getClass());
	
	private MessageSourceAccessor messageAccessor;
	
	private String nameKey;
	
	private String descriptionKey;
	
	public AbstractDataObject(MessageSourceAccessor messageAccessor, String nameKey, String descriptionKey) {
		this.messageAccessor = messageAccessor;
		this.nameKey = nameKey;
		this.descriptionKey = descriptionKey;
		
		validateMessages(LocaleContextHolder.getLocale());
	}

	protected void validateMessages(Locale locale) {
		
		if (getName(locale).equals(nameKey)) {
			logger.warn(String.format("No message with the key [%s] and the %s locale", nameKey, locale.toString()));
		}
		
		if (getDescription(locale).equals(descriptionKey)) {
			logger.warn(String.format("No message with the key [%s] and the %s locale", descriptionKey, locale.toString()));
		}
	}
	
	/*package*/ MessageSourceAccessor getMessageAccessor() {
		return messageAccessor;
	}
	
	/*package*/ String getNameKey() {
		return nameKey;
	}

	/*package*/ String getDescriptionKey() {
		return descriptionKey;
	}

	public String getName() {
		return getName(LocaleContextHolder.getLocale());
	}
	
	protected String getName(Locale locale) {
		try {
		
			String name = messageAccessor.getMessage(nameKey, locale);
			return StringUtils.hasText(name) ? name : nameKey;
		
		} catch (NoSuchMessageException e) {
			return nameKey;
		}
	}

	public String getDescription() {
		return getDescription(LocaleContextHolder.getLocale());
	}
	
	protected String getDescription(Locale locale) {
		try {
			
			String description = messageAccessor.getMessage(descriptionKey, locale);
			return StringUtils.hasText(description) ? description : descriptionKey;
		
		} catch (NoSuchMessageException e) {
			return descriptionKey;
		}
	}	
}
