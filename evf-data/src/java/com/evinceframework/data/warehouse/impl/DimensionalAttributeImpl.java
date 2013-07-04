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
package com.evinceframework.data.warehouse.impl;

import org.springframework.context.support.MessageSourceAccessor;

import com.evinceframework.data.warehouse.DimensionTable;
import com.evinceframework.data.warehouse.DimensionalAttribute;

public class DimensionalAttributeImpl<T> extends AbstractDataObject implements DimensionalAttribute<T> {

	private DimensionTableImpl dimension;
	
	private Class<T> valueType;
	
	private String columnName;
	
	public DimensionalAttributeImpl(MessageSourceAccessor messageAccessor, String nameKey, String descriptionKey,
			DimensionTableImpl dimension, String columnName, Class<T> valueType) {
		this(messageAccessor, nameKey, descriptionKey, dimension, columnName, valueType, false);
	}
	
	public DimensionalAttributeImpl(MessageSourceAccessor messageAccessor, String nameKey, String descriptionKey,
			DimensionTableImpl dimension, String columnName, Class<T> valueType, boolean isBusinessKey) {
		super(messageAccessor, nameKey, descriptionKey);
		
		this.dimension = dimension;
		this.columnName = columnName;
		this.valueType = valueType;
		
		dimension.addDimensionalAttribute(this, isBusinessKey);
	}

	@Override
	public Class<T> getValueType() {
		return valueType;
	}

	@Override
	public DimensionTable getDimensionTable() {
		return dimension;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

}
