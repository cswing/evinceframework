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

import com.evinceframework.membership.authentication.AuthenticationProviderImpl;
import com.evinceframework.membership.model.User;

/**
 * Implementations of this interface provide a salt to be used when hashing a password.  When
 * building a salt, only immutable parameters on the user or any other object should be used. 
 *  
 * @author Craig Swing
 * 
 * @see AuthenticationProviderImpl
 */
public interface SaltProvider {

	/**
	 * Calculate a salt to use to hash a password for the given user. 
	 * 
	 * @param user The user.
	 * @return A salt to use when hashing a password for the given user.
	 */
	public byte[] determineSalt(User user);
	
}
