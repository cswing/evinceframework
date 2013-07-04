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
package com.evinceframework.data.tests.warehouse.query;

import junit.framework.TestCase;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5Dialect;

import com.evinceframework.data.tests.warehouse.TestData;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.Query;
import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.data.warehouse.query.impl.FactSelectionImpl;
import com.evinceframework.data.warehouse.query.impl.QueryEngineImpl;
import com.evinceframework.data.warehouse.query.impl.QueryImpl;
import com.evinceframework.data.warehouse.query.impl.QueryResultImpl;

public class QueryEngineTests extends TestCase {
	
	public void testNullFactSelections() {
		
		TestQueryEngine queryEngine = new TestQueryEngine(new MySQL5Dialect());
		QueryImpl query = new QueryImpl(TestData.factTable, null);
		query.setMaximumRowCount(null);
		
		try {
			
			queryEngine.generateSqlForTest(query);
			fail("QueryException  expected.");
			
		} catch (QueryException e) {}
	}
	
	public void testEmptyFactSelections() {
		
		TestQueryEngine queryEngine = new TestQueryEngine(new MySQL5Dialect());
		QueryImpl query = new QueryImpl(TestData.factTable, new FactSelection[]{});
		query.setMaximumRowCount(null);
		
		try {
			
			queryEngine.generateSqlForTest(query);
			fail("QueryException  expected.");
			
		} catch (QueryException e) {}
	}
	
	public void testQuerySql() throws QueryException {
		
		TestQueryEngine queryEngine = new TestQueryEngine(new MySQL5Dialect());
		QueryImpl query = new QueryImpl(TestData.factTable, new FactSelection[] {
				new FactSelectionImpl(TestData.simpleIntegerFact)
		});
		query.setMaximumRowCount(null);
		
		String sql = queryEngine.generateSqlForTest(query);
		
		assertNotNull(sql);
		assertEquals("select fact.simpleInteger as simpleInteger from fooTable fact", sql);
	}
	
	public void testQueryEngineLimitSql() throws QueryException {
		
		TestQueryEngine queryEngine = new TestQueryEngine(new MySQL5Dialect(), 10000);
		QueryImpl query = new QueryImpl(TestData.factTable, new FactSelection[] {
				new FactSelectionImpl(TestData.simpleIntegerFact)
		});
		query.setMaximumRowCount(null);
		
		String sql = queryEngine.generateSqlForTest(query);
		
		assertNotNull(sql);
		assertEquals("select fact.simpleInteger as simpleInteger from fooTable fact limit ?", sql);
	}
	
	public static class TestQueryEngine extends QueryEngineImpl {

		public TestQueryEngine(Dialect dialect) {
			super(dialect, null);
		}
		
		public TestQueryEngine(Dialect dialect, Integer rowLimit) {
			super(dialect, rowLimit);
		}
		
		public String generateSqlForTest(Query query) throws QueryException {			
			return this.generateSql(query, new QueryResultImpl(query)).sql;
		}
	}
}
