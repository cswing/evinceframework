/*
 * Copyright 2013 the original author or authors.
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
package com.evinceframework.web.messaging.exceptions;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.evinceframework.core.factory.MapBackedClassLookupFactory;
import com.evinceframework.web.dojo.mvc.model.ViewModelUtils;
import com.evinceframework.web.messaging.UserMessage;

/**
 * Handles exceptions by converting them to {@link UserMessage}s to be displayed to the user. 
 * 
 * @author Craig Swing
 *
 * @see AbstractHandlerExceptionResolver 
 * @see UserMessage
 * @see UserMessageTransform
 */
public class UserMessageExceptionResolver extends AbstractHandlerExceptionResolver {

	private static final Log logger = LogFactory.getLog(UserMessageExceptionResolver.class);

	public static final String DEFAULT_VIEW_NAME = "system.error";

	private String viewName = DEFAULT_VIEW_NAME;

	private ViewModelUtils modelUtils = new ViewModelUtils();
	
	private MapBackedClassLookupFactory<UserMessageTransform> transformFactory;

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public MapBackedClassLookupFactory<UserMessageTransform> getTransformFactory() {
		return transformFactory;
	}

	public void setTransformFactory(MapBackedClassLookupFactory<UserMessageTransform> transformFactory) {
		this.transformFactory = transformFactory;
	}

	@Override
	protected ModelAndView doResolveException(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

		logger.error("Handling an exception.", ex);

		ModelAndView mnv = new ModelAndView(viewName);
		UserMessageTransform transform = transformFactory.lookup(ex.getClass());
		modelUtils.findOrCreateViewModel(mnv.getModel()).addAll(Arrays.asList(transform.transform(ex)));

		return mnv;
	}

}
