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

/**
 * Provides the ability to customize to the default behavior of the library using a properties
 * files.  The default values are specified in default.properties. 
 * 
 * @author Craig Swing
 */
public class Configuration {

	private int attemptsBeforeAccountLockedOut;
	
	private int lockoutDuration;
	
	private int accountExpirationDuration;
	
	private String rolePrefix;

	/**
	 * The number of failed attempts since the last successful login that can occur before the
	 * account is considered locked out.  An account becomes unlocked once the 
	 * {@link #getLockoutDuration()} has passed.  
	 * 
	 * @return the number of failed attempts before locking an account out.
	 */
	public int getAttemptsBeforeAccountLockedOut() {
		return attemptsBeforeAccountLockedOut;
	}

	public void setAttemptsBeforeAccountLockedOut(int attemptsBeforeAccountLockedOut) {
		this.attemptsBeforeAccountLockedOut = attemptsBeforeAccountLockedOut;
	}

	/**
	 * The duration in seconds that a user's account is considered locked out. Once the 
	 * {@link #getAttemptsBeforeAccountLockedOut()} has been reached, the user must wait the 
	 * duration specified by this value before attempting to authenticate again.
	 * 
	 * @return the duration of the lockout period, in seconds.
	 */
	public int getLockoutDuration() {
		return lockoutDuration;
	}

	public void setLockoutDuration(int lockoutDuration) {
		this.lockoutDuration = lockoutDuration;
	}

	/**
	 * An account will automatically expire once the number of days specified by this value pass
	 * since the last successful login. A negative value or zero, will be interpreted as never 
	 * setting an expiration date on an account.
	 * 
	 * @return the number of days before account expiration.
	 */
	public int getAccountExpirationDuration() {
		return accountExpirationDuration;
	}

	public void setAccountExpirationDuration(int accountExpiration) {
		this.accountExpirationDuration = accountExpiration;
	}

	/**
	 * Spring security assumes that the their granted authorities begin with "ROLE_" by default.   
	 * 
	 * @return the prefix
	 */
	public String getRolePrefix() {
		return rolePrefix;
	}

	public void setRolePrefix(String rolePrefix) {
		this.rolePrefix = rolePrefix;
	}
	
}
