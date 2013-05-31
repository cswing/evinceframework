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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.evinceframework.membership.model.User;

public class AuthenticationLogger implements AuthenticationObserver {
	
	public static final String LOGGER_NAME = "evf.membership.authentication";
	
	private Log logger = LogFactory.getLog(LOGGER_NAME);
	
	@Override
	public void onSuccessfulAuthentication(User user) {
		// TODO log IPAddress
		logger.info(String.format("User %s successfully authenticated.", user.getEmailAddress()));
	}

	@Override
	public void onFailedAuthenticationAttempt(User user, String username, String message) {
		
		// TODO log IPAddress
		if(user == null) {
			logger.info(String.format(
					"User %s unsuccessfully attempted to authenticate - user is not known.", 
					username));
		} else {
			logger.info(String.format(
					"User %s unsuccessfully attempted to authenticate - incorrect password.", 
					username));
		}
	}

	@Override
	public void onAuthenticationServiceFailure(
			User user, String username, String message, Exception e) {
		
		logger.error(String.format(
				"User %s unsuccessfully attempted to authenticate - incorrect password.", 
				username), e);
	}

}
