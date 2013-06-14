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

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

import com.evinceframework.web.messaging.UserMessage;
import com.evinceframework.web.messaging.exceptions.UserMessageTransform;

/**
 * An abstract implementation of {@link UserMessageTransform} that provides the convenience of
 * using a {@link Set} of {@link UserMessage}s instead of an {@link Array}.
 * 
 * @author Craig Swing
 *
 * @see UserMessageTransform
 * @see UserMessage
 */
public abstract class AbstractUserMessageTransform implements UserMessageTransform {

	public static final String DEFAULT_VIEW_NAME = "evf.system.error";

	private String viewName = DEFAULT_VIEW_NAME;
	
	@Override
	public UserMessage[] transform(Throwable t) {
		Set<UserMessage> messages = new HashSet<UserMessage>();		
		onTransform(t, messages);				
		return messages.toArray(new UserMessage[] {});
	}
	
	@Override
	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * Perform the transform.
	 * 
	 * @param t
	 * @param messages
	 */
	protected abstract void onTransform(Throwable t, Set<UserMessage> messages);

}
