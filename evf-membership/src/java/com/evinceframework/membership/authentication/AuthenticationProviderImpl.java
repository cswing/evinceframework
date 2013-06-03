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

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.evinceframework.membership.SaltProvider;
import com.evinceframework.membership.PasswordHasher;
import com.evinceframework.membership.SecurityMessageSource;
import com.evinceframework.membership.model.SecurityRight;
import com.evinceframework.membership.model.User;

/**
 * An implementation of {@link AuthenticationProvider} that uses JPA backed objects to perform
 * security and membership functions.
 * 
 * @author Craig Swing
 *
 * @see AuthenticationProvider
 * @see User
 * @see SecurityRight
 */
public class AuthenticationProviderImpl extends AbstractUserDetailsAuthenticationProvider {

	private static final Log logger = LogFactory.getLog(AuthenticationProviderImpl.class);
	
	private MessageSourceAccessor messageAccessor;
	
	private Set<AuthenticationObserver> observers = new HashSet<AuthenticationObserver>();
	
	private PasswordHasher hasher = new PasswordHasher();
		
	private UserQuery query;
	
	private SaltProvider saltProvider = new SaltProviderImpl();
	
	/**
	 * The {@link AuthenticationObserver}s that will be notified of certain authentication events.
	 * 
	 * @return the observers.
	 */
	public Set<AuthenticationObserver> getObservers() {
		return observers;
	}

	public void setObservers(Set<AuthenticationObserver> observers) {
		this.observers = observers;
	}

	/**
	 * The {@link SaltProvider} that will create a salt for hashing passwords.
	 * 
	 * @return the salt provider
	 */
	public SaltProvider getSaltProvider() {
		return saltProvider;
	}

	public void setSaltProvider(SaltProvider saltProvider) {
		this.saltProvider = saltProvider;
	}
	
	/**
	 * Messages used by this provider.
	 * 
	 * @return the message source accessor.
	 * @see {@link SecurityMessageSource}
	 */
	public MessageSourceAccessor getMessageSource() {
		return messageAccessor;
	}

	public void setMessageSource(MessageSourceAccessor messageAccessor) {
		this.messageAccessor = messageAccessor;
	}

	/**
	 * The hashing utility used to hash passwords.
	 * 
	 * @return the password hasher.
	 */
	public PasswordHasher getHasher() {
		return hasher;
	}

	public void setHasher(PasswordHasher hasher) {
		this.hasher = hasher;
	}

	/**
	 * The query used to find the user who is attempting to authenticate.
	 * 
	 * @return the query.
	 */
	public UserQuery getQuery() {
		return query;
	}

	public void setQuery(UserQuery query) {
		this.query = query;
	}

	@Override
	protected void additionalAuthenticationChecks(
			UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) 
					throws AuthenticationException { }

	@Override	
	protected UserDetails retrieveUser(
			String username, UsernamePasswordAuthenticationToken authentication) 
					throws AuthenticationException {		
		
		Locale locale = Locale.US;
		
		User user = query.findByEmailAddress(username);
		if(user == null) {
			
			String message = messages.getMessage(
					SecurityMessageSource.UNKNOWN_USER, new Object[] { username }, locale);
					
			notifyFailedAuthenticationAttempt(null, username, message);
			throw new UsernameNotFoundException(message);
		}
		
		byte[] salt = saltProvider.determineSalt(user);
		
		try {
			if (!hasher.verifyPassword(
					authentication.getCredentials().toString(), salt, user.getPassword())) {
				
				String message = messages.getMessage(
						SecurityMessageSource.BAD_CREDENTIALS, new Object[] { username }, locale);
				
				notifyFailedAuthenticationAttempt(user, username, message);
				throw new BadCredentialsException(message);
			}
		
		} catch (NoSuchAlgorithmException e) {
			
			String message = messages.getMessage(
					SecurityMessageSource.SERVICE_FAILURE, new Object[] { username }, locale);
			
			notifyAuthenticationServiceFailure(user, username, message, e);
			throw new AuthenticationServiceException(message, e);
		
		} catch (UnsupportedEncodingException e) {
			
			String message = messages.getMessage(
					SecurityMessageSource.SERVICE_FAILURE, new Object[] { username }, locale);
			
			notifyAuthenticationServiceFailure(user, username, message, e);
			throw new AuthenticationServiceException(message, e);
		}
				
		notifySuccessfulAuthentication(user);
		
		return user;
	}
	
	protected void notifyFailedAuthenticationAttempt(User user, String username, String message) {
		if(observers == null)
			return;
		
		for(AuthenticationObserver observer : observers) {
			try {
				observer.onFailedAuthenticationAttempt(user, username, message);
				
			} catch (Exception e) {
				logger.error("An error occurred invoking an observer.", e);
			}
		}
	}
	
	protected void notifySuccessfulAuthentication(User user) {
		if(observers == null)
			return;
		
		for(AuthenticationObserver observer : observers) {
			try {
				observer.onSuccessfulAuthentication(user);
				
			} catch (Exception e) {
				logger.error("An error occurred invoking an observer.", e);
			}
		}
	}
	
	protected void notifyAuthenticationServiceFailure(
			User user, String username, String message, Exception serviceException) {
		
		if(observers == null)
			return;
		
		for(AuthenticationObserver observer : observers) {
			try {
				observer.onAuthenticationServiceFailure(user, username, message, serviceException);
				
			} catch (Exception e) {
				logger.error("An error occurred invoking an observer.", e);
			}
		}
	}
	
	public class SaltProviderImpl implements SaltProvider {

		private Base64 encoder = new Base64();
		
		@Override
		public byte[] determineSalt(User user) {
			return encoder.encode(user.getObjectIdentifier().getBytes());
		}
	}
}