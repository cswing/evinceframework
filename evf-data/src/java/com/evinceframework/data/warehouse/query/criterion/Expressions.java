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
package com.evinceframework.data.warehouse.query.criterion;

import com.evinceframework.data.warehouse.Dimension;
import com.evinceframework.data.warehouse.DimensionalAttribute;
import com.evinceframework.data.warehouse.Fact;

/**
 * A facade used to create different types of expressions.
 * 
 * @author Craig Swing
 *
 */
public class Expressions {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression eq(Dimension dimension, DimensionalAttribute<?> attribute, Object value) {
		return new DimensionCriterion(ComparisonOperator.EQUALS, dimension, attribute, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression notEq(Dimension dimension, DimensionalAttribute<?> attribute, Object value) {
		return new DimensionCriterion(ComparisonOperator.NOT_EQUALS, dimension, attribute, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression greaterThen(Dimension dimension, DimensionalAttribute<?> attribute, Object value) {
		return new DimensionCriterion(ComparisonOperator.GREATER_THAN, dimension, attribute, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression greaterThenOrEqual(Dimension dimension, DimensionalAttribute<?> attribute, Object value) {
		return new DimensionCriterion(ComparisonOperator.GREATER_THAN_OR_EQUALS, dimension, attribute, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression lessThen(Dimension dimension, DimensionalAttribute<?> attribute, Object value) {
		return new DimensionCriterion(ComparisonOperator.LESS_THAN, dimension, attribute, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression lessThenOrEqual(Dimension dimension, DimensionalAttribute<?> attribute, Object value) {
		return new DimensionCriterion(ComparisonOperator.LESS_THAN_OR_EQUALS, dimension, attribute, value);
	}
	
	
	// FactCriterion
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression eq(Fact<?> fact, Object value) {
		return new FactRangeCriterion(ComparisonOperator.EQUALS, fact, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression notEq(Fact<?> fact, Object value) {
		return new FactRangeCriterion(ComparisonOperator.NOT_EQUALS, fact, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression greaterThen(Fact<?> fact, Object value) {
		return new FactRangeCriterion(ComparisonOperator.GREATER_THAN, fact, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression greaterThenOrEqual(Fact<?> fact, Object value) {
		return new FactRangeCriterion(ComparisonOperator.GREATER_THAN_OR_EQUALS, fact, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression lessThen(Fact<?> fact, Object value) {
		return new FactRangeCriterion(ComparisonOperator.LESS_THAN, fact, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Expression lessThenOrEqual(Fact<?> fact, Object value) {
		return new FactRangeCriterion(ComparisonOperator.LESS_THAN_OR_EQUALS, fact, value);
	}
	
	// Logical operators
	
	public static Expression and(Expression lh, Expression rh, Expression ... others) {
		
		Expression le = new LogicalExpression.And(lh, rh);
		
		for(Expression expr : others) {
			le = and(le, expr);
		}
		
		return le;
	}
	
	public static Expression or(Expression lh, Expression rh, Expression ... others) {
		
		Expression le = new LogicalExpression.Or(lh, rh);
		
		for(Expression expr : others) {
			le = or(le, expr);
		}
		
		return le;
	}
}
