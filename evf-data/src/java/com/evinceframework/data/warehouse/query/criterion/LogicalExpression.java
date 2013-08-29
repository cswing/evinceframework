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

/**
 * Defines logical operations that can be used when specifying criteria for queries.
 * 
 * @author Craig Swing
 */
public abstract class LogicalExpression implements Expression {
	
	private Expression rhs;
	
	private Expression lhs;
	
	private String expression;

	private LogicalExpression(Expression lhs, Expression rhs, String expression) {
		this.rhs = rhs;
		this.lhs = lhs;
		this.expression = expression;
	}

	public Expression getRightHandExpression() {
		return rhs;
	}

	public Expression getLeftHandExpression() {
		return lhs;
	}

	public String getExpression() {
		return expression;
	}
	
	public static class And extends LogicalExpression {
		
		public static final String OPERATOR = "AND";
		
		/* package */ And(Expression lhs, Expression rhs) {
			super(lhs, rhs, OPERATOR);
		}
	}
	
	public static class Or extends LogicalExpression {
		
		public static final String OPERATOR = "OR";
		
		/* package */ Or(Expression lhs, Expression rhs) {
			super(lhs, rhs, OPERATOR);
		}
	}
}
