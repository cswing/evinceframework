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
import com.evinceframework.data.warehouse.FactTable;

public class FactTableImpl extends AbstractDataObject implements FactTable {
	
	private String tableName;
	
	private Fact<? extends Object>[] facts = new Fact<?>[]{};
	
	private Dimension[] dimensions = new Dimension[] {};
	
	public FactTableImpl(MessageSourceAccessor messageAccessor, String nameKey, String descriptionKey, String tableName) {
		super(messageAccessor, nameKey, descriptionKey);
		this.tableName = tableName;
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

	/*package*/ void addFact(FactImpl<? extends Object> fact) {
		List<Fact<? extends Object>> f = new LinkedList<Fact<? extends Object>>(Arrays.asList(facts));
		assert(fact.getFactTable().equals(this));
		f.add(fact);
		facts = f.toArray(new Fact<?>[]{});
	}
	
	/*package*/ void addDimension(Dimension dimension) {
		List<Dimension> d = new LinkedList<Dimension>(Arrays.asList(dimensions));
		assert(dimension.getFactTable().equals(this));
		d.add(dimension);
		dimensions = d.toArray(new Dimension[]{});
	}
}
