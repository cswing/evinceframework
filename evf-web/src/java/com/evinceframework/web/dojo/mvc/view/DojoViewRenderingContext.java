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

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.evinceframework.web.dojo.mvc.view.config.DojoConfiguration;

public class DojoViewRenderingContext {
	
	private DojoConfiguration configuration;
	
	private Map<String, ?> model;
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	/* package */ DojoViewRenderingContext(DojoConfiguration configuration, Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		this.configuration = configuration;
		this.model = model;
		this.request = request;
		this.response = response;
	}
	
	public DojoConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(DojoConfiguration configuration) {
		this.configuration = configuration;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public Map<String, ?> getModel() {
		return model;
	}

	public void setModel(Map<String, ?> model) {
		this.model = model;
	}

}
