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
package com.evinceframework.web.messaging.exceptions;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;

import com.evinceframework.core.factory.MapBackedClassLookupFactory;
import com.evinceframework.web.messaging.exceptions.transforms.BindingTransform;
import com.evinceframework.web.messaging.exceptions.transforms.SingleMessageTransform;

/**
 * 
 * 
 * @author Craig Swing
 *
 */
public class UserMessageTransformFactory extends MapBackedClassLookupFactory<UserMessageTransform>
		implements InitializingBean {

	private MessageSource messageSource;
	
	public UserMessageTransformFactory() {
		setDefaultImplementation(new SingleMessageTransform<Throwable>());
	}
	
	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		if (!getLookupMap().containsKey(BindException.class)) {
			getLookupMap().put(BindException.class, new BindingTransform(this.messageSource));
		}
	}

	
	
}