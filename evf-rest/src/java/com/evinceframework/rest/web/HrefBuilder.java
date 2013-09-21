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
package com.evinceframework.rest.web;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.StringUtils;

public class HrefBuilder implements InitializingBean {

	@Autowired
	private HttpServletRequest request;
	
	private String contextPath;
	
	private URL currentHref;

	@Override
	public void afterPropertiesSet() throws Exception {
		
		try {
			contextPath = request.getContextPath();
			currentHref = new URL(UrlUtils.buildFullRequestUrl(request));
			
		} catch (MalformedURLException e) {
			throw new RuntimeException("Unable to instantiate a response.", e);
		}
		
	}

	public URL getCurrentHref() {
		return currentHref;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String buildFullUrl(String relativeUrl) {
		try {
			
			String url = relativeUrl;
			if(StringUtils.hasLength(contextPath)) {
				url = String.format("%s%s", contextPath, url);
			}
			
			return new URL(currentHref, url).toExternalForm();

		} catch (MalformedURLException e) {
			throw new RuntimeException("Unable to build url", e);
		}	
	}
}
