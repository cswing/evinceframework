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
package com.evinceframework.membership;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.evinceframework.membership.authentication.AuthenticationProviderImpl;

/**
 * A resource bundle containing messages that are needed as part of the authentication process.
 * 
 * @author Craig Swing
 * 
 * @see AuthenticationProviderImpl
 */
public class SecurityMessageSource extends ResourceBundleMessageSource {

	private static final String BASE_NAME = 
			"com.evinceframework.membership.i18n.evf_membership_messages_en_US.properties";
	
	/**
	 * A key used to retrieve the unknown user message.
	 */
	public static final String UNKNOWN_USER = "authentication.unknownUser";
	
	/**
	 * A key used to retrieve the bad credentials message.
	 */
	public static final String BAD_CREDENTIALS = "authentication.badCredentials";
	
	/**
	 * A key used to retrieve the service failure message.
	 */
	public static final String SERVICE_FAILURE = "authentication.serviceFailure";
	
	/**
	 * Default constructor
	 */
    public SecurityMessageSource() {
    	setBasename(BASE_NAME);
    }
    
    /**
     * Utility method to retrieve the MessageSourceAccessor.
     * 
     * @return the MessageSourceAccessor
     */
    public static MessageSourceAccessor getAccessor() {
    	return new MessageSourceAccessor(new SecurityMessageSource());
    }
}
