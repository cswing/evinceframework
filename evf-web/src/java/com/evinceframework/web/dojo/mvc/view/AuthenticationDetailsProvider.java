/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evinceframework.web.dojo.mvc.view;

import java.util.Set;

/**
 * Provides authentication details that gets passed to the web client.
 * 
 * @author Craig Swing
 * 
 * @see DojoView
 * @see DojoViewResolver
 */
public interface AuthenticationDetailsProvider<T> {

	/**
	 * A set of security rights that the currently authenticated user has.  If the user is not 
	 * authenticated, then this should return the set of rights that an a anonymous user has.
	 * 
	 * @return security rights.
	 */
	public Set<String> getSecurityRights();

	/**
	 * Details about the user.
	 * 
	 * @return
	 */
	public T getUserDetails();
	
}
