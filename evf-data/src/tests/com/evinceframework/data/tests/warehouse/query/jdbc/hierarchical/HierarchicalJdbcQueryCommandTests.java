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
package com.evinceframework.data.tests.warehouse.query.jdbc.hierarchical;

import junit.framework.TestCase;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5Dialect;

import com.evinceframework.data.tests.mocks.MockDataSource;
import com.evinceframework.data.tests.warehouse.TestData;
import com.evinceframework.data.warehouse.query.DrillPathEntry;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.FactSelectionFunction;
import com.evinceframework.data.warehouse.query.HierarchicalQuery;
import com.evinceframework.data.warehouse.query.HierarchicalQueryResult;
import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.data.warehouse.query.criterion.Criterion;
import com.evinceframework.data.warehouse.query.impl.FactSelectionImpl;
import com.evinceframework.data.warehouse.query.jdbc.HierarchicalJdbcQueryCommand;
import com.evinceframework.data.warehouse.query.jdbc.SqlQueryBuilder;

public class HierarchicalJdbcQueryCommandTests extends TestCase {

	public void testNullFactSelections() {
		
		DrillPathEntry<String> root = new DrillPathEntry<String>(TestData.dimensionA, TestData.dimensionalAttrA1);
		
		TestHierarchicalQueryCommand queryEngine = new TestHierarchicalQueryCommand(new MySQL5Dialect());
		HierarchicalQuery query = new HierarchicalQuery(
				TestData.factTable,
				null,
				new Criterion[]{},
				root
			);
		
		try {
			
			queryEngine.generateSqlForTest(query);
			fail("QueryException  expected.");
			
		} catch (QueryException e) {}
	}
	
	public void testEmptyFactSelections() {
		
		DrillPathEntry<String> root = new DrillPathEntry<String>(TestData.dimensionA, TestData.dimensionalAttrA1);
		TestHierarchicalQueryCommand queryEngine = new TestHierarchicalQueryCommand(new MySQL5Dialect());
		HierarchicalQuery query = new HierarchicalQuery(
				TestData.factTable,
				new FactSelection[]{},
				new Criterion[]{},
				root
			);
		
		try {
			
			queryEngine.generateSqlForTest(query);
			fail("QueryException  expected.");
			
		} catch (QueryException e) {}
	}
	
	public void testFactSelectionWithoutFunction() {
		
		DrillPathEntry<String> root = new DrillPathEntry<String>(TestData.dimensionA, TestData.dimensionalAttrA1);
		TestHierarchicalQueryCommand queryEngine = new TestHierarchicalQueryCommand(new MySQL5Dialect());
		HierarchicalQuery query = new HierarchicalQuery(
				TestData.factTable,
				new FactSelection[]{
						new FactSelectionImpl(TestData.simpleIntegerFact)
				},
				new Criterion[]{},
				root
			);
		
		try {
			
			queryEngine.generateSqlForTest(query);
			fail("QueryException  expected.");
			
		} catch (QueryException e) {}
	}
	
	public void testQuerySql() throws QueryException {
		
		DrillPathEntry<String> root = new DrillPathEntry<String>(TestData.dimensionA, TestData.dimensionalAttrA1);
		TestHierarchicalQueryCommand queryEngine = new TestHierarchicalQueryCommand(new MySQL5Dialect());
		HierarchicalQuery query = new HierarchicalQuery(
				TestData.factTable,
				new FactSelection[]{
						new FactSelectionImpl(TestData.simpleIntegerFact, FactSelectionFunction.SUM)
				},
				new Criterion[]{},
				root
			);
		
		String sql = queryEngine.generateSqlForTest(query);
		
		assertNotNull(sql);
		assertEquals("select SUM(fact.simpleInteger) as SUM_simpleInteger, dim_0.attr1 as dim_0_attr1 from fooTable fact inner join dimA dim_0 on dimA1_id=dim_0.dimA_id group by dim_0.attr1", sql);
	}
	
	public static class TestHierarchicalQueryCommand extends HierarchicalJdbcQueryCommand {

		public TestHierarchicalQueryCommand(Dialect dialect) {
			super(new MockDataSource(), dialect);
		}
		
		public String generateSqlForTest(HierarchicalQuery query) throws QueryException {	
			return this.generateSql(query, new HierarchicalQueryResult(query), 
					new SqlQueryBuilder(query, getDialect()));
		}
	}
	
}
