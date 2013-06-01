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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import com.evinceframework.web.messaging.UserMessage;
import com.evinceframework.web.messaging.UserMessageType;

/**
 * Transforms a {@link Throwable} to a single {@link UserMessage}.
 * 
 * @author Craig Swing
 * 
 * @see AbstractUserMessageTransform
 * @see Throwable
 * @see UserMessage
 */
public class SingleMessageTransform<T extends Throwable> extends AbstractUserMessageTransform {

	private boolean showStackTrace = false;
	
	private String defaultErrorCode = "SYS-0000";
	
	/**
	 * Whether or not the stack trace should be included in the description of the user message.
	 * 
	 * @return true if the stack trace should be included in the description, otherwise false.
	 */
	public boolean isShowStackTrace() {
		return showStackTrace;
	}

	public void setShowStackTrace(boolean showStackTrace) {
		this.showStackTrace = showStackTrace;
	}
	
	/**
	 * The error code to use by default when creating the {@link UserMessage}.
	 * 
	 * @return the default error code
	 */
	public String getDefaultErrorCode() {
		return defaultErrorCode;
	}

	public void setDefaultErrorCode(String defaultErrorCode) {
		this.defaultErrorCode = defaultErrorCode;
	}

	@Override
	@SuppressWarnings("unchecked")	
	protected final void onTransform(Throwable throwable, Set<UserMessage> messages) {		
		T t = (T)throwable;		
		messages.add(new UserMessage(
				UserMessageType.ERROR, getErrorCode(t), getMessage(t), getDescription(t)));		
	}

	/**
	 * Determines the error code based on the {@link Throwable}.  The default implementation is to return
	 * the code provided by {@link #getDefaultErrorCode()}. 
	 * 
	 * @param throwable
	 * @return the error code
	 */
	protected String getErrorCode(T throwable) {
		return defaultErrorCode;
	}
	
	/**
	 * Determines the error code based on the {@link Throwable}.  The default implementation is to return
	 * the message provided by {@link Throwable#getLocalizedMessage()}.
	 * 
	 * @param throwable
	 * @return the message
	 */
	protected String getMessage(T throwable) {
		return throwable.getLocalizedMessage();
	}
	
	/**
	 * Determines the error code based on the {@link Throwable}.  The default implementation is to return
	 * the stack trace, if {@link #isShowStackTrace()} is set to true.
	 *  
	 * @param throwable
	 * @return the description.
	 */
	protected String getDescription(T throwable) {
		
		String desc = "";
		
		if (showStackTrace) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			desc = sw.toString();
		}
		
		return desc;
	}
	
}
