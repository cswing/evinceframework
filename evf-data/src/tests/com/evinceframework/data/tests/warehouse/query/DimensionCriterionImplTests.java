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
import com.evinceframework.data.warehouse.query.DimensionCriterion;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.data.warehouse.query.SummarizationAttribute;
import com.evinceframework.data.warehouse.query.impl.DimensionCriterionImpl;
import com.evinceframework.data.warehouse.query.impl.FactSelectionImpl;
import com.evinceframework.data.warehouse.query.impl.QueryImpl;

public class DimensionCriterionImplTests extends TestCase {

	public void test_QueryEngine() throws QueryException {
			
		TestQueryEngine queryEngine = new QueryEngineTests.TestQueryEngine(new MySQL5Dialect());
		
		QueryImpl query = new QueryImpl(TestData.factTable, 
			new FactSelection[] {
				new FactSelectionImpl(TestData.simpleIntegerFact)
			},
			new SummarizationAttribute[] {},
			new DimensionCriterion[]{
				new DimensionCriterionImpl(TestData.dimensionA, 5)
			}
		);
		query.setMaximumRowCount(null);
		
		String sql = queryEngine.generateSqlForTest(query);
		
		assertNotNull(sql);
		assertEquals("select fact.simpleInteger as simpleInteger from fooTable fact where fact.dimA1_id = ?", sql);
	}
	
}
