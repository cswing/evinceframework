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
package com.evinceframework.web.dojo.mvc.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

import com.evinceframework.web.dojo.json.JsonStoreEngine;
import com.evinceframework.web.dojo.mvc.view.config.DojoConfigurationResolver;

public class DojoViewResolver extends AbstractCachingViewResolver {

	private JsonStoreEngine jsonEngine;
	
	private String[] storeNames;
	
	private AuthenticationDetailsProvider<?> authenticationDetailsProvider = 
			new AuthenticationDetailsProviderImpl();
	
	private DojoConfigurationResolver configurationResolver;
		
	public DojoConfigurationResolver getConfigurationResolver() {
		return configurationResolver;
	}

	public void setConfigurationResolver(DojoConfigurationResolver configurationResolver) {
		this.configurationResolver = configurationResolver;
	}
 
	public JsonStoreEngine getJsonEngine() {
		return jsonEngine;
	}

	public void setJsonEngine(JsonStoreEngine jsonEngine) {
		this.jsonEngine = jsonEngine;
	}

	public String[] getStoreNames() {
		return storeNames;
	}

	public void setStoreNames(String[] storeNames) {
		this.storeNames = storeNames;
	}
	
	public AuthenticationDetailsProvider<?> getAuthenticationDetailsProvider() {
		return authenticationDetailsProvider;
	}

	public void setAuthenticationDetailsProvider(
			AuthenticationDetailsProvider<?> authenticationDetailsProvider) {
		this.authenticationDetailsProvider = authenticationDetailsProvider;
	}

	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		
		DojoLayout layout = (DojoLayout)getApplicationContext().getBean(getViewBeanName(viewName), DojoLayout.class);
		if (layout == null) {
			// TODO logging
			return null;
		}
		
		return new DojoView(jsonEngine, storeNames, configurationResolver, layout, authenticationDetailsProvider);
	}

	protected String getViewBeanName(String viewName) {
		return String.format("dojoView.%s", viewName.replace("/", "."));
	}
	
	private class AuthenticationDetailsProviderImpl 
			implements AuthenticationDetailsProvider<Map<String, Object>> {
		
		@Override
		public Set<String> getSecurityRoles() {
			return new HashSet<String>();
		}
		
		@Override
		public Map<String, Object> getUserDetails() {
			return new HashMap<String, Object>();
		}
	}
	
}
