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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.evinceframework.membership.Configuration;
import com.evinceframework.membership.model.User;

/**
 * Responsible for updating the {@link User} based on authentication events as defined by
 * {@link IAuthenticationObserver}.
 * 
 * @author Craig Swing
 */
public class UserUpdater implements IAuthenticationObserver {

	@PersistenceContext
	private EntityManager entityManager;
	
	private Configuration configuration;
	
	/**
	 * The membership configuration.
	 *  
	 * @return
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void onSuccessfulAuthentication(User user) {
		
		user.setFailedAuthenticationAttempts(0);
		user.setLastSuccessfulAuthentication(utcNow());
		
		if(configuration.getAccountExpirationDuration() <=0) {
			user.setAccountExpirationDate(null);
		} else {
			user.setAccountExpirationDate(
					utcNow().plusDays(configuration.getAccountExpirationDuration()));
		}
		
		entityManager.merge(user);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void onFailedAuthenticationAttempt(User user, String username, String message) {
		
		user.setFailedAuthenticationAttempts(user.getFailedAuthenticationAttempts() + 1);
		user.setLastFailedAuthentication(utcNow());
		
		entityManager.merge(user);
	}

	@Override
	public void onAuthenticationServiceFailure(
			User user, String username, String message,Exception e) {}
	
	protected DateTime utcNow() {
		return DateTime.now(DateTimeZone.UTC);
	}
}
