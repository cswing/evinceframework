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
package com.evinceframework.web.dojo.navigation;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.evinceframework.web.dojo.mvc.model.ViewModelUtils;

/**
 * A {@link HandlerInterceptor} that allows for a {@link Navigator} to be added 
 * to the view model for any request.
 * 
 * The {@link Navigator} is provided by a {@link NavigationProvider}.
 * 
 * @author Craig Swing
 */
public class NavigationInterceptor extends HandlerInterceptorAdapter {

	private static final Log logger = LogFactory.getLog(NavigationInterceptor.class);
	
	private ViewModelUtils modelUtils = new ViewModelUtils(); 
	
	private NavigationProvider provider;
	
	public NavigationProvider getProvider() {
		return provider;
	}

	public void setProvider(NavigationProvider provider) {
		this.provider = provider;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
		
		List<Object> viewModel = 
				modelUtils.findOrCreateViewModel(modelAndView.getModelMap());
		
		if (provider == null) {
			logger.debug("Cannot add a navigator to the view model because no provider was set.");
		
		} else {
			viewModel.add(provider.getNavigator());
		}
	}	
}
