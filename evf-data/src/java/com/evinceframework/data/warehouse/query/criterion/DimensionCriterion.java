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

import com.evinceframework.data.warehouse.Dimension;
import com.evinceframework.data.warehouse.DimensionalAttribute;

public class DimensionCriterion<T> extends AbstractCriterion {

	private Dimension dimension;
	
	private DimensionalAttribute<T> attribute;
	
	private T value;
	
	protected DimensionCriterion(ComparisonOperator operator, Dimension dimension, DimensionalAttribute<T> attribute, T value) {
	
		super(operator);
		
		this.dimension = dimension;
		this.attribute = attribute;
		this.value = value;
	}

	public Dimension getDimension() {
		return dimension;
	}
	
	public DimensionalAttribute<T> getDimensionalAttribute() {
		return attribute;
	}
	
	public T getValue() {
		return value;
	}
}
