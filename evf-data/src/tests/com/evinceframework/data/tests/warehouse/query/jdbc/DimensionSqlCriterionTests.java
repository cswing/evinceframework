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
package com.evinceframework.data.tests.warehouse.query.jdbc;

import junit.framework.TestCase;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5Dialect;

import com.evinceframework.data.tests.warehouse.TestData;
import com.evinceframework.data.warehouse.Dimension;
import com.evinceframework.data.warehouse.DimensionalAttribute;
import com.evinceframework.data.warehouse.FactTable;
import com.evinceframework.data.warehouse.query.AbstractQuery;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.criterion.ComparisonOperator;
import com.evinceframework.data.warehouse.query.criterion.DimensionCriterion;
import com.evinceframework.data.warehouse.query.jdbc.SqlQueryBuilder;
import com.evinceframework.data.warehouse.query.jdbc.criterion.DimensionSqlCriterion;
import com.evinceframework.data.warehouse.query.jdbc.criterion.SqlCriterionContext;

public class DimensionSqlCriterionTests extends TestCase {

	private static final Dialect DIALECT = new MySQL5Dialect();
	
	private static final SqlCriterionContext CONTEXT = new SqlCriterionContext(
			new SqlQueryBuilder(new TestQuery(TestData.factTable), DIALECT));
	
	private static final DimensionSqlCriterion CRITERION = new DimensionSqlCriterion();
	
	public void testEqualsExpression() {
		
		DimensionCriterion<?> expression = new TestCriterion<String>(
				ComparisonOperator.EQUALS, TestData.dimensionA, TestData.dimensionalAttrA1, "XYZ");
		
		assertEquals("dim_0.attr1 = ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public void testNotEqualsExpression() {
		
		DimensionCriterion<?> expression = new TestCriterion<String>(
				ComparisonOperator.NOT_EQUALS, TestData.dimensionA, TestData.dimensionalAttrA1, "XYZ");
		
		assertEquals("dim_0.attr1 <> ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public void testLessThanExpression() {
		
		DimensionCriterion<?> expression = new TestCriterion<String>(
				ComparisonOperator.LESS_THAN, TestData.dimensionA, TestData.dimensionalAttrA1, "XYZ");
		
		assertEquals("dim_0.attr1 < ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public void testLessThanOrEqualsExpression() {
		
		DimensionCriterion<?> expression = new TestCriterion<String>(
				ComparisonOperator.LESS_THAN_OR_EQUALS, TestData.dimensionA, TestData.dimensionalAttrA1, "XYZ");
		
		assertEquals("dim_0.attr1 <= ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public void testGreaterThanExpression() {
		
		DimensionCriterion<?> expression = new TestCriterion<String>(
				ComparisonOperator.GREATER_THAN, TestData.dimensionA, TestData.dimensionalAttrA1, "XYZ");
		
		assertEquals("dim_0.attr1 > ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public void testGreaterThanOrEqualsExpression() {
		
		DimensionCriterion<?> expression = new TestCriterion<String>(
				ComparisonOperator.GREATER_THAN_OR_EQUALS, TestData.dimensionA, TestData.dimensionalAttrA1, "XYZ");
		
		assertEquals("dim_0.attr1 >= ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public static class TestQuery extends AbstractQuery {
		protected TestQuery(FactTable factTable) {
			super(factTable, new FactSelection[]{});
		}
	}
	
	public static class TestCriterion<T extends Object> extends DimensionCriterion<T> {
		TestCriterion(ComparisonOperator operator, Dimension dimension, DimensionalAttribute<T> attribute, T value) {
			super(operator, dimension, attribute, value);
		}
	}
}
