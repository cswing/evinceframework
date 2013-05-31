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
package com.evinceframework.membership.authentication;

import com.evinceframework.membership.model.User;

/**
 * Implementations of this interface will observe authentication events.
 * 
 * @author Craig Swing
 * 
 * @see AuthenticationProviderImpl
 */
public interface AuthenticationObserver {

	/**
	 * A user has successfully authenticated.
	 * 
	 * @param user the user who authenticated.
	 */
	public void onSuccessfulAuthentication(User user);
	
	/**
	 * An authentication attempt with invalid credentials was attempted.
	 * 
	 * If the user is null, it means that the username that was used to attempt a login, does not 
	 * correlate to a user in the system.
	 * 
	 * If the user is not null, then the credentials supplied did not match what was in the 
	 * data store.
	 * 
	 * @param user The user who attempted authentication.  Can be null.
	 * @param username The username that was used for authentication.
	 * @param message A message related to the failure.
	 */
	public void onFailedAuthenticationAttempt(User user, String username, String message);
	
	/**
	 * An unknown error occurred when attempting to authenticate the credentials.
	 * 
	 * @param user The user who attempted authentication.
	 * @param username The username that was used for authentication.
	 * @param message A message related to the failure.
	 * @param exc The exception.
	 */
	public void onAuthenticationServiceFailure(
			User user, String username, String message, Exception e);
}
