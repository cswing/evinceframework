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

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.evinceframework.web.dojo.json.JsonStoreEngine;
import com.evinceframework.web.dojo.mvc.model.ViewModelUtils;

public class JsonStoreView implements View {

	private ViewModelUtils modelUtils = new ViewModelUtils();
	
	private JsonStoreEngine jsonEngine;

	public JsonStoreView(JsonStoreEngine jsonEngine) {
		this.jsonEngine = jsonEngine;
	}

	@Override
	public String getContentType() {
		return "application/json";
	}

	@Override
	@SuppressWarnings("unchecked")
	public void render(Map<String, ?> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Set<Object> viewModel = 
				modelUtils.findOrCreateViewModel((Map<String, Object>)model);
		
		jsonEngine.serialize(response.getOutputStream(), viewModel);
	}
}
