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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.evinceframework.jpa.BaseEntity;
import com.evinceframework.membership.Configuration;

/**
 * A JPA backed implementation of {@link UserDetails}. Users and their {@link SecurityRight}s are
 * stored in a database.
 * 
 * @author Craig Swing
 *
 */
@Configurable("evf.membership.user")
@Entity(name="evf_membership_user")
public class User extends BaseEntity implements UserDetails {

	private static final long serialVersionUID = -5895224222841274717L;
	
	private Configuration configuration;
	
	private String displayName;
	
	private String emailAddress;
	
	private String password;
	
	private DateTime accountExpirationDate;
	
	private DateTime passwordExpirationDate;
	
	private boolean enabled;
	
	private int failedAuthenticationAttempts = 0;
	
	private DateTime lastSuccessfulAuthentication;
	
	private DateTime lastFailedAuthentication;
	
	private Set<SecurityRight> rights = new HashSet<SecurityRight>();
	
	@Transient
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	@Column(name="displayName", length=64, nullable=false)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(name="emailAddress", length=254, nullable=false, unique=true)
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	@Transient
	public String getUsername() {		
		return emailAddress;
	}

	@Override
	@Column(name="password", length=256, nullable=true)
	public String getPassword() {								
		return password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	@Column(name="account_expiration_dt", nullable=true)
	public DateTime getAccountExpirationDate() {
		return accountExpirationDate;
	}

	public void setAccountExpirationDate(DateTime accountExpirationDate) {
		this.accountExpirationDate = accountExpirationDate;
	}

	@Column(name="password_expiration_dt", nullable=true)
	public DateTime getPasswordExpirationDate() {
		return passwordExpirationDate;
	}

	public void setPasswordExpirationDate(DateTime passwordExpirationDate) {
		this.passwordExpirationDate = passwordExpirationDate;
	}

	@Override
	@Transient
	public boolean isAccountNonExpired() {		
		return accountExpirationDate == null || accountExpirationDate.isAfterNow();
	}

	@Override
	@Transient
	public boolean isAccountNonLocked() {		
		return !isAccountLocked();
	}
	
	/**
	 * An account is locked if the number of failed attempts has surpassed the alloted number 
	 * of failed attempts as specified by {@link Configuration#getAttemptsBeforeAccountLockedOut()}
	 * and the last login attempt is within the time frame specified by 
	 * {@link Configuration#getLockoutDuration()}.
	 * 
	 * @return true if the account is locked, otherwise false.
	 */
	@Transient
	public boolean isAccountLocked() {
		return failedAuthenticationAttempts > configuration.getAttemptsBeforeAccountLockedOut();
	}

	/**
	 * The number of failed attempts since the last authentication.
	 * 
	 * @return the number of failed attempts.
	 */
	@Column(name="failed_authentication_attempts", nullable=false)
	public int getFailedAuthenticationAttempts() {
		return failedAuthenticationAttempts;
	}

	public void setFailedAuthenticationAttempts(int failedAuthenticationAttempts) {
		this.failedAuthenticationAttempts = failedAuthenticationAttempts;
	}

	/**
	 * The date/time of the last successful authentication.
	 * 
	 * @return
	 */
	@Column(name="last_authentication_dt", nullable=true)
	public DateTime getLastSuccessfulAuthentication() {
		return lastSuccessfulAuthentication;
	}

	public void setLastSuccessfulAuthentication(DateTime lastSuccessfulAuthentication) {
		this.lastSuccessfulAuthentication = lastSuccessfulAuthentication;
	}
	
	/**
	 * The date/time of the last failed authentication.
	 * 
	 * @return
	 */
	@Column(name="last_failed_authentication_dt", nullable=true)
	public DateTime getLastFailedAuthentication() {
		return lastFailedAuthentication;
	}

	public void setLastFailedAuthentication(DateTime lastFailedAuthentication) {
		this.lastFailedAuthentication = lastFailedAuthentication;
	}

	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return passwordExpirationDate == null || passwordExpirationDate.isAfterNow();
	}

	@Override
	@Column(name="enabled", nullable=false)
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "evf_membership_userright", 
	            joinColumns = { @JoinColumn(name = "user_id")}, 
	            inverseJoinColumns={@JoinColumn(name="right_id")})
	public Set<SecurityRight> getRights() {
		return rights;
	}

	public void setRights(Set<SecurityRight> rights) {
		this.rights = rights;
	}

	@Override
	@Transient
	public Collection<GrantedAuthority> getAuthorities() {
		
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("USER"));
		
		for(SecurityRight r : rights) {
			authorities.add(r);
		}
		
		return Collections.unmodifiableCollection(authorities);			
	}
}