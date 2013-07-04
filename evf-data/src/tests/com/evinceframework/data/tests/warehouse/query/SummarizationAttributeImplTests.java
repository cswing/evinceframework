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

import org.hibernate.dialect.MySQL5Dialect;

import com.evinceframework.data.tests.warehouse.TestData;
import com.evinceframework.data.tests.warehouse.query.QueryEngineTests.TestQueryEngine;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.FactSelectionFunction;
import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.data.warehouse.query.SummarizationAttribute;
import com.evinceframework.data.warehouse.query.impl.FactSelectionImpl;
import com.evinceframework.data.warehouse.query.impl.QueryImpl;
import com.evinceframework.data.warehouse.query.impl.SummarizationAttributeImpl;

public class SummarizationAttributeImplTests extends TestCase {

	public void testSummarizationAttributeQuery() throws QueryException {
		
		TestQueryEngine queryEngine = new TestQueryEngine(new MySQL5Dialect());
		QueryImpl query = new QueryImpl(TestData.factTable, 
			new FactSelection[] {
				new FactSelectionImpl(TestData.simpleIntegerFact, FactSelectionFunction.SUM)
			},
			new SummarizationAttribute[] {
				new SummarizationAttributeImpl(TestData.dimensionA)
			}
		);
		query.setMaximumRowCount(null);
		
		String sql = queryEngine.generateSqlForTest(query);
		
		assertNotNull(sql);
		assertEquals("select dim_0.attr1 as attr1, SUM(fact.simpleInteger) as SUM_simpleInteger from fooTable fact inner join dimA dim_0 on dimA1_id=dim_0.dimA_id group by dim_0.attr1", sql);
	}
}
