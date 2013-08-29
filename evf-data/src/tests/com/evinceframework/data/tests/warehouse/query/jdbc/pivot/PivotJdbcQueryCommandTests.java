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
package com.evinceframework.data.tests.warehouse.query.jdbc.pivot;

import junit.framework.TestCase;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5Dialect;

import com.evinceframework.data.tests.mocks.MockDataSource;
import com.evinceframework.data.tests.warehouse.TestData;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.PivotQuery;
import com.evinceframework.data.warehouse.query.PivotQueryResult;
import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.data.warehouse.query.SummarizationAttribute;
import com.evinceframework.data.warehouse.query.criterion.Expression;
import com.evinceframework.data.warehouse.query.impl.FactSelectionImpl;
import com.evinceframework.data.warehouse.query.jdbc.PivotJdbcQueryCommand;
import com.evinceframework.data.warehouse.query.jdbc.SqlQueryBuilder;

public class PivotJdbcQueryCommandTests extends TestCase {
	
	public void testNullFactSelections() {
		
		TestPivotQueryCommand queryEngine = new TestPivotQueryCommand(new MySQL5Dialect());
		PivotQuery query = new PivotQuery(
				TestData.factTable,
				null,
				new Expression[]{},
				new SummarizationAttribute[] {}
			);
		
		query.setMaximumRowCount(null);
		
		try {
			
			queryEngine.generateSqlForTest(query);
			fail("QueryException  expected.");
			
		} catch (QueryException e) {}
	}
	
	public void testEmptyFactSelections() {
		
		TestPivotQueryCommand queryEngine = new TestPivotQueryCommand(new MySQL5Dialect());
		PivotQuery query = new PivotQuery(
				TestData.factTable,
				new FactSelection[]{},
				new Expression[]{},
				new SummarizationAttribute[] {}
			);
		query.setMaximumRowCount(null);
		
		try {
			
			queryEngine.generateSqlForTest(query);
			fail("QueryException  expected.");
			
		} catch (QueryException e) {}
	}
	
	public void testQuerySql() throws QueryException {
		
		TestPivotQueryCommand queryEngine = new TestPivotQueryCommand(new MySQL5Dialect());
		PivotQuery query = new PivotQuery(
				TestData.factTable,
				new FactSelection[]{ new FactSelectionImpl(TestData.simpleIntegerFact) },
				new Expression[]{},
				new SummarizationAttribute[] {}
			); 
				
		query.setMaximumRowCount(null);
		
		String sql = queryEngine.generateSqlForTest(query);
		
		assertNotNull(sql);
		assertEquals("select fact.simpleInteger as fact_simpleInteger from fooTable fact", sql);
	}
	
	public void testQueryEngineLimitSql() throws QueryException {
		
		TestPivotQueryCommand queryEngine = new TestPivotQueryCommand(new MySQL5Dialect(), 10000);
		PivotQuery query = new PivotQuery(
				TestData.factTable,
				new FactSelection[]{ new FactSelectionImpl(TestData.simpleIntegerFact) },
				new Expression[]{},
				new SummarizationAttribute[] {}
			);
		
		query.setMaximumRowCount(null);
		
		String sql = queryEngine.generateSqlForTest(query);
		
		assertNotNull(sql);
		assertEquals("select fact.simpleInteger as fact_simpleInteger from fooTable fact limit ?", sql);
	}
	
	public static class TestPivotQueryCommand extends PivotJdbcQueryCommand {

		public TestPivotQueryCommand(Dialect dialect) {
			super(new MockDataSource(), dialect, null);
		}
		
		public TestPivotQueryCommand(Dialect dialect, Integer rowLimit) {
			super(new MockDataSource(), dialect, rowLimit);
		}
		
		public String generateSqlForTest(PivotQuery query) throws QueryException {	
			return this.generateSql(query, new PivotQueryResult(query), 
					new SqlQueryBuilder(query, getDialect())).sql;
		}
	}
}
