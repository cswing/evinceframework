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
package com.evinceframework.rest.web.model;

import com.evinceframework.rest.web.HrefBuilder;

public class ModelResponse extends AbstractResponse {

	private Object model;
	
	public ModelResponse(HrefBuilder builder, Object model) {
		super(builder);
		
		this.model = model;
	}

	public Object getModel() {
		return model;
	}	
}
