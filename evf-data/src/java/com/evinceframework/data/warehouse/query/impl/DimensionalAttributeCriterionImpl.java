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

import com.evinceframework.data.warehouse.Dimension;
import com.evinceframework.data.warehouse.DimensionalAttribute;
import com.evinceframework.data.warehouse.query.DimensionCriterion;

/**
 * Assumes that a dimension attribute is a string or number.
 * 
 * @author Craig Swing
 *
 * @param <T>
 */
public class DimensionalAttributeCriterionImpl<T> extends CriterionSupport<T> implements DimensionCriterion {

	private Dimension dimension;
	
	private DimensionalAttribute<T> attribute;
	
	public DimensionalAttributeCriterionImpl(Dimension dimension, DimensionalAttribute<T> attribute, T... values) {
		super(attribute.getValueType(), values);
		
		this.dimension = dimension;
		this.attribute = attribute;
	}

	@Override
	public Dimension getDimension() {
		return dimension;
	}

	@Override
	public boolean requiresJoinOnDimensionTable() {
		return true;
	}

	@Override
	public String createWhereFragment(String factTableAlias, String dimensionTableAlias) {
		return String.format(createWhereFormat(), 
				String.format("%s.%s", dimensionTableAlias, attribute.getColumnName()));
	}

}
