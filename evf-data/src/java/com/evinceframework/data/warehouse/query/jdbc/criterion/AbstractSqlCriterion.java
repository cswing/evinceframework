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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.internal.util.StringHelper;

import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.data.warehouse.query.criterion.ComparisonOperator;
import com.evinceframework.data.warehouse.query.criterion.Criterion;
import com.evinceframework.data.warehouse.query.jdbc.ParameterValueSetterFactory;

public abstract class AbstractSqlCriterion<T extends Criterion> implements SqlCriterion {

	protected final ParameterValueSetterFactory parameterSupport = ParameterValueSetterFactory.DEFAULT_FACTORY;
	
	private static final Map<ComparisonOperator, String> operatorMap = new HashMap<ComparisonOperator, String>();
	
	static {
		operatorMap.put(ComparisonOperator.EQUALS, "=");
		operatorMap.put(ComparisonOperator.NOT_EQUALS, "<>");
		operatorMap.put(ComparisonOperator.GREATER_THAN, ">");
		operatorMap.put(ComparisonOperator.GREATER_THAN_OR_EQUALS, ">=");
		operatorMap.put(ComparisonOperator.LESS_THAN, "<");
		operatorMap.put(ComparisonOperator.LESS_THAN_OR_EQUALS, "<=");
	}
	
	@Override
	public abstract Class<?> getCriterionClass();
	
	@Override
	@SuppressWarnings("unchecked")
	public String createWhereClause(SqlCriterionContext context, Criterion criterion) {
		return onCreateWhereClause(context, (T) criterion);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public int setExpressionParameters(SqlCriterionContext context, Criterion criterion, PreparedStatement stmt, int paramIdx) 
			throws SQLException, QueryException {
		return onSetParameters(context, (T) criterion, stmt, paramIdx);
	}
	
	public abstract String onCreateWhereClause(SqlCriterionContext context, T criterion);
	
	public abstract int onSetParameters(SqlCriterionContext context, T criterion, PreparedStatement stmt, int paramIdx) 
			throws SQLException, QueryException;

	protected String getSqlComparison(ComparisonOperator operator) {
		
		if(operatorMap.containsKey(operator))
			return operatorMap.get(operator);
		
		return "";
	}
	
	protected String createSingularWhereClause(String alias, String column, String comparisonOperator) {
		return String.format("%s.%s %s ?", alias, column, comparisonOperator);
	}
		
	public static String joinClauses(List<String> clauses, String logicalOperator) {
		
		if(clauses.size() == 0)
			return null;
		
		if(clauses.size() == 1)
			return clauses.get(0);
		
		return String.format("(%s)", StringHelper.join(
				String.format(" %s ", logicalOperator), clauses.toArray(new String[]{})));
	}
}
