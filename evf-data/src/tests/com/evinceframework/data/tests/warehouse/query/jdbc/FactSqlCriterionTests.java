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
import com.evinceframework.data.warehouse.Fact;
import com.evinceframework.data.warehouse.FactTable;
import com.evinceframework.data.warehouse.query.AbstractQuery;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.criterion.ComparisonOperator;
import com.evinceframework.data.warehouse.query.criterion.FactRangeCriterion;
import com.evinceframework.data.warehouse.query.jdbc.SqlQueryBuilder;
import com.evinceframework.data.warehouse.query.jdbc.criterion.FactSqlCriterion;
import com.evinceframework.data.warehouse.query.jdbc.criterion.SqlCriterionContext;

public class FactSqlCriterionTests extends TestCase {

	private static final Dialect DIALECT = new MySQL5Dialect();
	
	private static final SqlCriterionContext CONTEXT = new SqlCriterionContext(
			new SqlQueryBuilder(new TestQuery(TestData.factTable), DIALECT));
	
	private static final FactSqlCriterion CRITERION = new FactSqlCriterion();
	
	public void testEqualsExpression() {
		
		FactRangeCriterion<?> expression = 
				new TestCriterion<Integer>(ComparisonOperator.EQUALS, TestData.simpleIntegerFact, 1);
		
		assertEquals("fact.simpleInteger = ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public void testNotEqualsExpression() {
		
		FactRangeCriterion<?> expression = 
				new TestCriterion<Integer>(ComparisonOperator.NOT_EQUALS, TestData.simpleIntegerFact, 1);
		
		assertEquals("fact.simpleInteger <> ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public void testLessThanExpression() {
		
		FactRangeCriterion<?> expression = 
				new TestCriterion<Integer>(ComparisonOperator.LESS_THAN, TestData.simpleIntegerFact, 1);
		
		assertEquals("fact.simpleInteger < ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public void testLessThanOrEqualsExpression() {
		
		FactRangeCriterion<?> expression = 
				new TestCriterion<Integer>(ComparisonOperator.LESS_THAN_OR_EQUALS, TestData.simpleIntegerFact, 1);
		
		assertEquals("fact.simpleInteger <= ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public void testGreaterThanExpression() {
		
		FactRangeCriterion<?> expression = 
				new TestCriterion<Integer>(ComparisonOperator.GREATER_THAN, TestData.simpleIntegerFact, 1);
		
		assertEquals("fact.simpleInteger > ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public void testGreaterThanOrEqualsExpression() {
		
		FactRangeCriterion<?> expression = 
				new TestCriterion<Integer>(ComparisonOperator.GREATER_THAN_OR_EQUALS, TestData.simpleIntegerFact, 1);
		
		assertEquals("fact.simpleInteger >= ?", CRITERION.createWhereClause(CONTEXT, expression));
	}
	
	public static class TestQuery extends AbstractQuery {
		protected TestQuery(FactTable factTable) {
			super(factTable, new FactSelection[]{});
		}
	}
	
	public static class TestCriterion<T extends Number> extends FactRangeCriterion<T> {
		TestCriterion(ComparisonOperator operator, Fact<T> fact, T value) {
			super(operator, fact, value);
		}
	}
}
