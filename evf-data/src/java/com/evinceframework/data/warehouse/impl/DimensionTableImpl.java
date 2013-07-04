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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.support.MessageSourceAccessor;

import com.evinceframework.data.warehouse.DimensionTable;
import com.evinceframework.data.warehouse.DimensionalAttribute;

public class DimensionTableImpl extends AbstractDataObject implements DimensionTable {
	
	private String tableName;
	
	private String primaryKeyColumn;
	
	private DimensionalAttribute<? extends Object>[] businessKeys = new DimensionalAttribute<?>[]{};
	
	private DimensionalAttribute<? extends Object>[] attributes = new DimensionalAttribute<?>[]{};

	public DimensionTableImpl(MessageSourceAccessor messageAccessor, String nameKey, String descriptionKey,
			String tableName, String primaryKeyColumn) {		
		super(messageAccessor, nameKey, descriptionKey);
		
		this.tableName = tableName;
		this.primaryKeyColumn = primaryKeyColumn;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	
	@Override
	public DimensionalAttribute<? extends Object>[] getBusinessKey() {
		return businessKeys;
	}

	@Override
	public DimensionalAttribute<? extends Object>[] getAttributes() {
		return attributes;
	}
	
	/*package*/ void addDimensionalAttribute(DimensionalAttributeImpl<? extends Object> attr) {
		addDimensionalAttribute(attr, false);
	}
	
	/*package*/ void addDimensionalAttribute(DimensionalAttributeImpl<? extends Object> attr, boolean isBusinessKey) {
		assert(attr.getDimensionTable().equals(this));
		
		List<DimensionalAttribute<? extends Object>> a = 
				new LinkedList<DimensionalAttribute<? extends Object>>(Arrays.asList(attributes));
		a.add(attr);
		attributes = a.toArray(new DimensionalAttribute<?>[]{});
		
		if(isBusinessKey) {
			a = new LinkedList<DimensionalAttribute<? extends Object>>(Arrays.asList(businessKeys));
			a.add(attr);
			businessKeys = a.toArray(new DimensionalAttribute<?>[]{});
		}
	}
}
