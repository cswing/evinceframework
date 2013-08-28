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
public abstract class LogicalExpression implements Criterion {
	
	private Criterion rhs;
	
	private Criterion lhs;
	
	private String expression;

	private LogicalExpression(Criterion lhs, Criterion rhs, String expression) {
		this.rhs = rhs;
		this.lhs = lhs;
		this.expression = expression;
	}

	public Criterion getRightHandCriterion() {
		return rhs;
	}

	public Criterion getLeftHandCriterion() {
		return lhs;
	}

	public String getExpression() {
		return expression;
	}
	
	public static LogicalExpression and(Criterion lh, Criterion rh, Criterion ... others) {
		
		LogicalExpression le = new LogicalExpression.And(lh, rh);
		
		for(Criterion c : others) {
			le = and(le, c);
		}
		
		return le;
	}
	
	public static LogicalExpression or(Criterion lh, Criterion rh, Criterion ... others) {
		
		LogicalExpression le = new LogicalExpression.Or(lh, rh);
		
		for(Criterion c : others) {
			le = or(le, c);
		}
		
		return le;
	}

	public static class And extends LogicalExpression {
		
		private And(Criterion lhs, Criterion rhs) {
			super(lhs, rhs, "AND");
		}
	}
	
	public static class Or extends LogicalExpression {
		
		private Or(Criterion lhs, Criterion rhs) {
			super(lhs, rhs, "OR");
		}
	}
}
