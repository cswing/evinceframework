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

import org.springframework.http.HttpStatus;

import com.evinceframework.web.messaging.UserMessage;

/**
 * Implementations of this interface are responsible for transforming a {@link Throwable} 
 * into {@link UserMessage}s.
 * 
 * @author Craig Swing
 * 
 * @see UserMessage
 */
public interface UserMessageTransform {

	/**
	 * Transforms a {@link Throwable} into {@link UserMessage}s.
	 * 
	 * @param t the exception
	 * @return the messages
	 */
	public UserMessage[] transform(Throwable t);
	
	/**
	 * The view to use to render this error.
	 * 
	 * @return
	 */
	public String getViewName();
	
	/**
	 * The http status code that is associated with the {@link Throwable}.
	 * 
	 * @return
	 */
	public HttpStatus getHttpStatus();
}
