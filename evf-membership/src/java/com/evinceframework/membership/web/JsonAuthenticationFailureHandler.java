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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.evinceframework.core.factory.MapBackedClassLookupFactory;
import com.evinceframework.web.dojo.json.JsonStoreEngine;
import com.evinceframework.web.dojo.mvc.view.JsonStoreView;
import com.evinceframework.web.messaging.UserMessage;
import com.evinceframework.web.messaging.exceptions.UserMessageExceptionResolver;
import com.evinceframework.web.messaging.exceptions.UserMessageTransform;

/**
 * This handler will convert any exception to {@link UserMessage}s using {@link UserMessageTransform}s.  Afterward
 * it will serialize the {@link UserMessage}s to json using the {@link JsonStoreEngine}.
 * 
 * @author Craig Swing
 *
 * @see UserMessage
 * @see UserMessageTrasnform
 * @see JsonStoreEngine
 */
public class JsonAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private JsonStoreEngine jsonStoreEngine;
    
	private MapBackedClassLookupFactory<UserMessageTransform> transformFactory;
   
    public JsonStoreEngine getJsonStoreEngine() {
		return jsonStoreEngine;
	}

	public void setJsonStoreEngine(JsonStoreEngine jsonStoreEngine) {
		this.jsonStoreEngine = jsonStoreEngine;
	}

	public MapBackedClassLookupFactory<UserMessageTransform> getTransformFactory() {
    	return transformFactory;
    }

    public void setTransformFactory(MapBackedClassLookupFactory<UserMessageTransform> transformFactory) {
    	this.transformFactory = transformFactory;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                    AuthenticationException exception) throws IOException, ServletException {

    	UserMessageTransform t = transformFactory.lookup(exception.getClass());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(UserMessageExceptionResolver.MESSAGES_VARIABLE_NAME, t.transform(exception));
		
		try {
			new JsonStoreView(jsonStoreEngine).render(model, request, response);
		        
		} catch (Exception e) {
			throw new ServletException("Unable to serialize authentication failure messages.", e);
		}   
    }
}
