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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

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
	
	@Override
	public Set<String> getSecurityRights() {
		
		Set<String> rights = new HashSet<String>();
		
		Collection<GrantedAuthority> authorities = getCurrentUser().getAuthorities();
		for(GrantedAuthority ga : authorities) {
			rights.add(ga.getAuthority());
		}
		
		return rights;
	}

	@Override
	public Map<String, Object> getUserDetails() {
		
		User user = getCurrentUser();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", user.getDisplayName());
		map.put("login", user.getUsername());
		
		return map;
	}

	private User getCurrentUser() {
		
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof User) {
		  return (User)principal;
		}
		
		throw new RuntimeException("Current principal is not an instance of User.");
	}
	
}
