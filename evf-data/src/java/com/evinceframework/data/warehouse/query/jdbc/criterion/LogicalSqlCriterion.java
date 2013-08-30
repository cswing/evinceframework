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
package com.evinceframework.data.warehouse.query.jdbc.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.data.warehouse.query.criterion.Criterion;
import com.evinceframework.data.warehouse.query.criterion.LogicalExpression;

public class LogicalSqlCriterion extends AbstractSqlCriterion<LogicalExpression> {

	private static final Map<Class<? extends LogicalExpression>, String> operatorMap = 
			new HashMap<Class<? extends LogicalExpression>, String>();
	
	static {
		operatorMap.put(LogicalExpression.And.class, "AND");
		operatorMap.put(LogicalExpression.Or.class, "OR");
	}
	
	@Override
	public Class<?> getCriterionClass() {
		return LogicalExpression.class;
	}

	@Override
	public String onCreateWhereClause(SqlCriterionContext context, LogicalExpression expression) {
		List<String> clauses = new ArrayList<String>(); 
		clauses.add(context.getQueryBuilder().createWhereClause(context, expression.getLeftHandCriterion()));
		clauses.add(context.getQueryBuilder().createWhereClause(context, expression.getRightHandCriterion()));
		
		return joinClauses(clauses, expression.getExpression());
	}

	@Override
	public int onSetParameters(SqlCriterionContext context, LogicalExpression expression,
			PreparedStatement stmt, int paramIdx) throws SQLException, QueryException {
		
		int pi = paramIdx;
		
		pi += context.getQueryBuilder().setParameters(context, new Criterion[] {
				expression.getLeftHandCriterion(),
				expression.getRightHandCriterion()
		}, stmt, paramIdx);
		
		return pi;
	}

}
