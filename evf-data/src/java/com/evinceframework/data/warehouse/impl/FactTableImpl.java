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

import com.evinceframework.data.warehouse.Dimension;
import com.evinceframework.data.warehouse.Fact;
import com.evinceframework.data.warehouse.FactCategory;
import com.evinceframework.data.warehouse.FactTable;
import com.evinceframework.data.warehouse.query.QueryEngine;

public class FactTableImpl extends AbstractDataObject implements FactTable {
	
	private String tableName;
	
	private QueryEngine queryEngine;
	
	private Fact<? extends Object>[] facts = new Fact<?>[]{};
	
	private FactCategory[] factCategories = new FactCategoryImpl[]{};
	
	private Dimension[] dimensions = new Dimension[] {};
	
	public FactTableImpl(MessageSourceAccessor messageAccessor, String nameKey, String descriptionKey, 
			QueryEngine queryEngine, String tableName) {
		super(messageAccessor, nameKey, descriptionKey);
		this.tableName = tableName;
		this.queryEngine = queryEngine;
		
		this.queryEngine.addFactTable(this);
	}
	
	@Override
	public String getTableName() {
		return tableName;
	}
	
	@Override
	public Dimension[] getDimensions() {
		return dimensions;
	}

	@Override
	public Fact<? extends Object>[] getFacts() {
		return facts;
	}

	@Override
	public FactCategory[] getCategories() {
		return factCategories;
	}
	
	public QueryEngine getQueryEngine() {
		return queryEngine;
	}

	/*package*/ void addFact(FactImpl<? extends Object> fact) {
		List<Fact<? extends Object>> f = new LinkedList<Fact<? extends Object>>(Arrays.asList(facts));
		assert(fact.getFactTable().equals(this));
		f.add(fact);
		facts = f.toArray(new Fact<?>[]{});
	}
	
	/*package*/ void addFactCategory(FactCategoryImpl category) {
		List<FactCategory> f = new LinkedList<FactCategory>(Arrays.asList(factCategories));
		
		assert(category.getFactTable().equals(this));
		for(Fact<?> fact : category.getFacts())
			assert(fact.getFactTable().equals(this));
		
		f.add(category);
		factCategories = f.toArray(new FactCategory[]{});
	}
	
	/*package*/ void addDimension(Dimension dimension) {
		List<Dimension> d = new LinkedList<Dimension>(Arrays.asList(dimensions));
		assert(dimension.getFactTable().equals(this));
		d.add(dimension);
		dimensions = d.toArray(new Dimension[]{});
	}
	
	public Fact<?> findFact(String key) {
		if(key != null) {
			for(Fact<?> fact : getFacts()) {
				if (key.equals(fact.getColumnName())) {
					return fact;  
				}
			}
		}
		
		return null;
	}
	
	public Dimension findDimension(String key) {
		if(key != null) {
			for(Dimension d : getDimensions()) {
				if (key.equals(d.getForeignKeyColumn())) {
					return d;
				}
			}
		}
		
		return null;
	}
}
