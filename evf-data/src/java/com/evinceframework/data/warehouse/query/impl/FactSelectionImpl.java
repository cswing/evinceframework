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

import com.evinceframework.data.warehouse.Fact;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.FactSelectionFunction;

public class FactSelectionImpl implements FactSelection {

	private Fact<? extends Object> fact;
	
	private FactSelectionFunction function;
	
	public FactSelectionImpl(Fact<? extends Object> fact) {
		this(fact, null);
	}
	
	public FactSelectionImpl(Fact<? extends Object> fact, FactSelectionFunction function) {
		this.fact = fact;
		this.function = function;
	}

	@Override
	public Fact<? extends Object> getFact() {
		return fact;
	}

	@Override
	public FactSelectionFunction getFunction() {
		return function;
	}

}
