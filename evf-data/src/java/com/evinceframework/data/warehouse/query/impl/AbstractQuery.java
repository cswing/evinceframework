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
package com.evinceframework.data.warehouse.query.impl;

import java.util.Locale;

import com.evinceframework.data.warehouse.FactTable;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.Query;

public class AbstractQuery implements Query {

	private Locale locale;
	
	private FactTable factTable;
	
	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public FactTable getFactTable() {
		return factTable;
	}

	@Override
	public FactSelection[] getFactSelections() {
		// TODO Auto-generated method stub
		return null;
	}

}
