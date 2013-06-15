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
package com.evinceframework.membership.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.evinceframework.membership.Configuration;
import com.evinceframework.membership.model.AnonymousUser;
import com.evinceframework.membership.model.User;
import com.evinceframework.web.dojo.mvc.view.AuthenticationDetailsProvider;

/**
 * An implementation of {@link AuthenticationDetailsProvider} that returns data based on this 
 * membership library.
 * 
 * @author Craig Swing
 *
 * @see AuthenticationDetailsProvider
 */
public class AuthenticationDetailsProviderImpl implements AuthenticationDetailsProvider<Map<String, Object>> {
	
	private Configuration configuration;
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public Set<String> getSecurityRoles() {
		return getDelegate().getSecurityRoles();
	}

	@Override
	public Map<String, Object> getUserDetails() {
		return getDelegate().getUserDetails();
	}
	
	private AuthenticationDetailsProvider<Map<String, Object>> getDelegate() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication == null ? null : authentication.getPrincipal();

		if (principal instanceof User) {
			return new UserDetailsProvider((User)principal);
		}
		
		if (principal instanceof AnonymousUser) {
			return new AnonymousUserDetailsProvider((AnonymousUser)principal);
		}
		
		throw new RuntimeException("Current principal is not an instance of User or AnonymousUser.");
	}
	
	private class UserDetailsProvider implements AuthenticationDetailsProvider<Map<String, Object>> {

		private User user;
		
		public UserDetailsProvider(User user) {
			this.user = user;
		}
		
		@Override
		public Set<String> getSecurityRoles() {
			
			Set<String> roles = new HashSet<String>();
			
			Collection<GrantedAuthority> authorities = user.getAuthorities();
			for(GrantedAuthority ga : authorities) {
				
				String authority = ga.getAuthority();
				if(authority.startsWith(configuration.getRolePrefix())) {
					authority = authority.replaceFirst(configuration.getRolePrefix(), "");
				}
				
				roles.add(authority);
			}
			
			return roles;
		}

		@Override
		public Map<String, Object> getUserDetails() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", user.getDisplayName());
			map.put("login", user.getUsername());
			
			return map;
		}
		
	}
	
	private class AnonymousUserDetailsProvider implements AuthenticationDetailsProvider<Map<String, Object>> {

		private AnonymousUser user;
		
		public AnonymousUserDetailsProvider(AnonymousUser user) {
			this.user = user;
		}
		
		@Override
		public Set<String> getSecurityRoles() {
			
			Set<String> roles = new HashSet<String>();
			
			Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
			for(GrantedAuthority ga : authorities) {
				roles.add(ga.getAuthority());
			}
			
			return roles;
		}

		@Override
		public Map<String, Object> getUserDetails() {
			return null;
		}
		
	}
	
}
