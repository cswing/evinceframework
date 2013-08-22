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
package com.evinceframework.data.warehouse.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.evinceframework.core.factory.AbstractClassLookupFactory;
import com.evinceframework.data.warehouse.FactTable;
import com.evinceframework.data.warehouse.impl.FactTableImpl;

public class QueryEngine extends AbstractClassLookupFactory<QueryCommand<Query, QueryResult>> {

	private List<QueryCommand<Query, QueryResult>> commands = new ArrayList<QueryCommand<Query, QueryResult>>();
	
	private FactTable[] factTables = new FactTable[] {};
	
	public QueryEngine(List<QueryCommand<Query, QueryResult>> commands) {
		this.commands = commands;
	}

	public QueryResult query(Query query) throws QueryException {
		
		if(query == null)
			return null;
		
		QueryCommand<Query, QueryResult> cmd = lookup(query.getClass());
		if(cmd == null) {
			throw new QueryException(String.format("Unknown query: %s", query.getClass().getName())); // TODO i18n
		}
		
		return cmd.query(query);
	}
	
	/*package*/ public void addFactTable(FactTableImpl factTable) {
		List<FactTable> f = new LinkedList<FactTable>(Arrays.asList(factTables));
		assert(factTable.getQueryEngine().equals(this));
		f.add(factTable);
		factTables = f.toArray(new FactTable[]{});
	}

	@Override
	protected QueryCommand<Query, QueryResult> create(Class<?> clazz) {
		
		if(clazz == null)
			return null;
		
		for (QueryCommand<Query, QueryResult> cmd : commands) {
			if(clazz.equals(cmd.getQueryType())) {
				return cmd;
			}
		}
		
		return null;
	}
}
