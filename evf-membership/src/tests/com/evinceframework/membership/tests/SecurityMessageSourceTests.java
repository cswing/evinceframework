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
package com.evinceframework.membership.tests;

import org.springframework.context.support.MessageSourceAccessor;

import com.evinceframework.membership.SecurityMessageSource;

import junit.framework.TestCase;

public class SecurityMessageSourceTests extends TestCase {

	/*
	 * authentication.unknownUser={0} is not a registered email address.
authentication.badCredentials=Incorrect password provided for {0}.
authentication.serviceFailure=An internal error occurred attempting to authenticate {0}.
authentication.authenticationFailure=The email address and password you have entered is invalid.
	 * */
	
	public void testMessageRetrieval() {
		
		SecurityMessageSource source = new SecurityMessageSource();
		MessageSourceAccessor accessor = new MessageSourceAccessor(source); 
		
		assertEquals(accessor.getMessage(SecurityMessageSource.BAD_CREDENTIALS), 
				"Incorrect password provided for {0}.");
	}
	
}