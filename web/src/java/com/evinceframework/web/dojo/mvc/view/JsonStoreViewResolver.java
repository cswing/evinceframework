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

import java.util.Locale;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

import com.evinceframework.web.dojo.json.JsonStoreEngine;

public class JsonStoreViewResolver extends AbstractCachingViewResolver implements InitializingBean {

	private JsonStoreView defaultView;

	private JsonStoreEngine jsonEngine;

	public void setJsonEngine(JsonStoreEngine jsonEngine) {
		this.jsonEngine = jsonEngine;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		defaultView = new JsonStoreView(jsonEngine);
	}

	protected String getViewBeanName(String viewName) {
		return viewName;
	}

	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {

		ApplicationContext appContext = getApplicationContext();
		String beanName = getViewBeanName(viewName);

		if (appContext.containsBean(beanName) && appContext.isTypeMatch(beanName, View.class)) {
			return (View) appContext.getBean(beanName);
		}

		return defaultView;
	}

}
