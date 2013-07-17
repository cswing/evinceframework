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

import com.evinceframework.data.warehouse.Dimension;
import com.evinceframework.data.warehouse.DimensionTable;
import com.evinceframework.data.warehouse.FactTable;

public class DimensionImpl extends AbstractDataObject implements Dimension {

	private DimensionTableImpl table;
	
	private FactTableImpl factTable;
	
	private String foreignKeyColumn;
	
	public DimensionImpl(DimensionTableImpl table, FactTableImpl factTable, String foreignKeyColumn) {
		this(null, null, null, table, factTable, foreignKeyColumn);
	}
	
	public DimensionImpl(MessageSourceAccessor messageAccessor, String nameKey, String descriptionKey, 
			DimensionTableImpl table, FactTableImpl factTable, String foreignKeyColumn) {
		
		super(
				messageAccessor != null ? messageAccessor : table.getMessageAccessor(),
				nameKey != null ? nameKey : table.getNameKey(),
				descriptionKey != null ? descriptionKey : table.getDescriptionKey()
		);
		
		this.table = table;
		this.factTable = factTable;
		this.foreignKeyColumn = foreignKeyColumn;
		
		factTable.addDimension(this);
	}

	@Override
	public DimensionTable getTable() {
		return table;
	}

	@Override
	public FactTable getFactTable() {
		return factTable;
	}

	@Override
	public String getForeignKeyColumn() {
		return foreignKeyColumn;
	}

}