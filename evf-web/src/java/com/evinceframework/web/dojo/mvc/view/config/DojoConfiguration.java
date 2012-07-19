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
package com.evinceframework.web.dojo.mvc.view.config;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DojoConfiguration {

	// use when on urls for css and javascript.  Every restart of the server
	// will then reset the cache on the clients
	/**
	 * 
	 */
	private static final String DEFAULT_CACHE_KEY = String.valueOf(
			Calendar.getInstance().getTime().getTime());

	private String coreDojoPath;
	
	private String bodyCss;
	
	private String favoriteIconLink = "";
	
	private String cacheKey = DEFAULT_CACHE_KEY;
	
	private Map<String, String> modules = new HashMap<String, String>();
	
	private Set<String> requires = new HashSet<String>();

	private Map<String, String> parameters = new HashMap<String, String>();
	
	private Set<String> auxiliaryScriptPaths = new HashSet<String>();
	
	private Set<String> stylesheets = new HashSet<String>();
		
	public String getCoreDojoPath() {
		return coreDojoPath;
	}

	public void setCoreDojoPath(String coreDojoPath) {
		this.coreDojoPath = coreDojoPath;
	}

	public String getBodyCss() {
		return bodyCss;
	}

	public void setBodyCss(String bodyCss) {
		this.bodyCss = bodyCss;
	}

	public String getFavoriteIconLink() {
		return favoriteIconLink;
	}

	public void setFavoriteIconLink(String favoriteIconLink) {
		this.favoriteIconLink = favoriteIconLink;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public Map<String, String> getModules() {
		return modules;
	}

	public void setModules(Map<String, String> modules) {
		this.modules = modules;
	}

	public Set<String> getRequires() {
		return requires;
	}

	public void setRequires(Set<String> requires) {
		this.requires = requires;
	}

	public Map<String, String> getConfigParameters() {
		return parameters;
	}

	public void setConfigParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public Set<String> getAuxiliaryScriptPaths() {
		return auxiliaryScriptPaths;
	}

	public void setAuxiliaryScriptPaths(Set<String> auxiliaryScriptPaths) {
		this.auxiliaryScriptPaths = auxiliaryScriptPaths;
	}

	public Set<String> getStylesheets() {
		return stylesheets;
	}

	public void setStylesheets(Set<String> stylesheets) {
		this.stylesheets = stylesheets;
	}
	
}
