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
package com.evinceframework.data.warehouse.query.criterion;

import com.evinceframework.data.warehouse.Fact;

public class FactRangeCriterion<T extends Number> extends AbstractCriterion {

	private Fact<T> fact;
	
	private T value;

	@SuppressWarnings("unchecked")
	protected FactRangeCriterion(ComparisonOperator operator, Fact<T> fact, Object value) {
		super(operator);
		
		this.fact = fact;
		this.value = (T) value;
	}
	
	public Fact<T> getFact() {
		return fact;
	}

	public T getValue() {
		return value;
	}
}
