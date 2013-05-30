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
package com.evinceframework.membership.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;

import com.evinceframework.membership.authentication.AuthenticationProviderImpl;

/**
 * A JPA backed implementation of {@link GrantedAuthority}.  Security rights and their relationship 
 * to {@link User}s are stored in a database.
 * 
 * @author Craig Swing
 * 
 * @see GrantedAuthority
 * @see User
 * @see AuthenticationProviderImpl
 */
@Entity(name="evf_membership_right")
public class SecurityRight implements GrantedAuthority {

	private static final long serialVersionUID = 313449404324420067L;

	private String token;
	
	private String description;

	protected SecurityRight(){}
	
	/**
	 * A token that uniquely identifies the security right.
	 * 
	 * @return the token
	 */
	@Column(name="token", length=64, unique=true, nullable=false)
	public String getToken() {
		return token;
	}

	protected void setToken(String token) {
		this.token = token;
	}

	/**
	 * The description of the security right.
	 * 
	 * @return the description
	 */
	@Column(name="description", length=512, nullable=false)
	public String getDescription() {
		return description;
	}

	protected void setDescription(String description) {
		this.description = description;
	}

	@Override
	@Transient
	public String getAuthority() {
		return token;
	}	
}
