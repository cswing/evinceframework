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

import com.evinceframework.data.warehouse.Fact;
import com.evinceframework.data.warehouse.FactCategory;
import com.evinceframework.data.warehouse.FactTable;

public class FactCategoryImpl extends AbstractDataObject implements FactCategory {

	private FactTableImpl factTable;
	
	private Fact<?>[] facts = new Fact<?>[]{};
	
	public FactCategoryImpl(MessageSourceAccessor messageAccessor, String nameKey, String descriptionKey, FactTableImpl factTable) {
		super(messageAccessor, nameKey, descriptionKey);
		
		this.factTable = factTable;
		
		factTable.addFactCategory(this);
	}

	@Override
	public FactTable getFactTable() {
		return factTable;
	}
	
	/*package*/ FactTableImpl getFactTableImpl() {
		return factTable;
	}

	@Override
	public Fact<?>[] getFacts() {
		return facts;
	}

	/*package*/ void addFact(FactImpl<?> fact) {
		List<Fact<?>> l = new LinkedList<Fact<?>>(Arrays.asList(facts));
		assert(fact.getFactTable().equals(this.getFactTable()));
		l.add(fact);
		facts = l.toArray(new Fact<?>[]{});
	}
}
