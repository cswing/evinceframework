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
package com.evinceframework.rest.web.mvc;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.evinceframework.rest.web.HrefBuilder;
import com.evinceframework.rest.web.model.ErrorResponse;
import com.evinceframework.web.messaging.exceptions.UserMessageTransform;
import com.evinceframework.web.messaging.exceptions.UserMessageTransformFactory;

public class RESTControllerSupport {

	protected Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private HrefBuilder builder;

	@Autowired
	private UserMessageTransformFactory transformFactory;
	
	protected UserMessageTransformFactory getTransformFactory() {
		return transformFactory;
	}
	
	@ResponseBody
	@ExceptionHandler
	public Object handleException(Exception ex, HttpServletRequest req, HttpServletResponse res) {
		
		UserMessageTransform transform = transformFactory.lookup(ex.getClass());
		
		res.setStatus(transform.getHttpStatus().value());
		return new ErrorResponse(builder, Arrays.asList(transform.transform(ex)));
	}
	
}
