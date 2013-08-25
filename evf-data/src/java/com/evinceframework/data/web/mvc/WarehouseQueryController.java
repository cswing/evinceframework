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
package com.evinceframework.data.web.mvc;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.evinceframework.data.warehouse.query.Query;
import com.evinceframework.data.warehouse.query.QueryEngine;
import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.web.mvc.ControllerSupport;
import com.evinceframework.web.stereotype.ServiceController;

/**
 * A service controller that provides access to the warehouse query engines.
 * 
 * @author Craig Swing
 *
 */
@Controller
@ServiceController
public class WarehouseQueryController 
		extends ControllerSupport implements BeanFactoryAware {

	private static final String ERROR_VIEW = "warehouse.query.error";
	
	private static final String QUERY_VIEW = "warehouse.query";
	
	private static final MessageSourceAccessor messages = new MessageSourceAccessor(new WebMessageSource());
	
	private BeanFactory beanFactory;
	
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@RequestMapping(method=RequestMethod.POST, value="/warehouse/query/{engine}")
	public String query(@PathVariable("engine")String engine, @RequestParam("query") Query query, ModelMap model) 
			throws QueryException {
		
		QueryEngine qEngine = beanFactory.getBean(engine, QueryEngine.class);
		if(qEngine == null) {
			addErrorMessage(model, messages.getMessage(WebMessageSource.UNKNOWN_ENGINE_KEY));
			return ERROR_VIEW;
		}
		
		modelUtil.addToViewModel(model, qEngine.query(query));
		
		return QUERY_VIEW;
	}
	
}
