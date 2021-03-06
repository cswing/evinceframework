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

import com.evinceframework.data.warehouse.Fact;
import com.evinceframework.data.warehouse.FactTable;

public class FactImpl<T> extends AbstractDataObject implements Fact<T> {

	private Class<T> valueType;
	
	private FactTableImpl table;
	
	private String columnName;
	
	public FactImpl(MessageSourceAccessor messageAccessor, String nameKey, String descriptionKey, 
			Class<T> valueType, FactCategoryImpl category, String columnName) {
		this(messageAccessor, nameKey, descriptionKey, valueType, category.getFactTableImpl(), columnName);
		
		category.addFact(this);
	}
	
	public FactImpl(MessageSourceAccessor messageAccessor, String nameKey, String descriptionKey, 
			Class<T> valueType, FactTableImpl table, String columnName) {
		super(messageAccessor, nameKey, descriptionKey);
		
		this.table = table;
		this.columnName = columnName;
		this.valueType = valueType;
		
		this.table.addFact(this);
	}
	
	@Override
	public FactTable getFactTable() {
		return table;
	}
	
	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public Class<T> getValueType() {
		return valueType;
	}
}
