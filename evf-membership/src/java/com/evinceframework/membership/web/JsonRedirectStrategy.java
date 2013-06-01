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
package com.evinceframework.membership.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * When making an xhr request (ajax) and the result of the http request is a redirect, 
 * the default behavior of web browsers is to perform the redirect on the ajax request.
 * 
 * The common desired behavior for authentication that is performed via an asynchronous request
 * is to redirect what the user is looking at and not redirect the asynchronous request.  
 * 
 * This {@link RedirectStrategy} will write a json object to the response.  It will be 
 * the responsibility of the consuming javascript to perform the redirection.
 * 
 * The format of the json returned is { code: 302, url: 'someUrl' } where someUrl is the url
 * to be redirected to.
 * 
 * @author Craig Swing
 * 
 * @see RedirectStrategy
 * @see DefaultRedirectStrategy
 */
public class JsonRedirectStrategy implements RedirectStrategy {

	private static final Log logger = LogFactory.getLog(JsonRedirectStrategy.class);

	private ObjectMapper mapper = new ObjectMapper();
	
	private boolean contextRelative;
	
	/**
	 * If <tt>true</tt>, causes any redirection URLs to be calculated minus the protocol
	 * and context path (defaults to <tt>false</tt>). 
	 *
	 * @return true if the context is relative, otherwise false
	 */
	public boolean getContextRelative() {
		return this.contextRelative;    
	}
	
	public void setContextRelative(boolean useRelativeContext) {
		this.contextRelative = useRelativeContext;
	}
	
	@Override
	public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
		redirectUrl = response.encodeRedirectURL(redirectUrl);

		if (logger.isDebugEnabled()) {
			logger.debug("Redirecting to '" + redirectUrl + "'");
		}
		
		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("code", 302);
		params.put("url", redirectUrl);
		response.getWriter().write(mapper.writeValueAsString(params));
	}

	protected String calculateRedirectUrl(String contextPath, String url) {
		if (!UrlUtils.isAbsoluteUrl(url)) {
			if (contextRelative) {
				return url;
			} else {
				return contextPath + url;
			}
		}
	
		// Full URL, including http(s)://
		if (!contextRelative) {
			return url;
		}
	
		// Calculate the relative URL from the fully qualified URL, minus the scheme and base context.
		url = url.substring(url.indexOf("://") + 3); // strip off scheme
		url = url.substring(url.indexOf(contextPath) + contextPath.length());
	
		if (url.length() > 1 && url.charAt(0) == '/') {
			url = url.substring(1);
		}
	
		return url;
	}	
}
