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
package com.evinceframework.data.warehouse.query.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.internal.util.StringHelper;

import com.evinceframework.core.factory.AbstractClassLookupFactory;
import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.data.warehouse.query.criterion.ComparisonOperator;
import com.evinceframework.data.warehouse.query.criterion.DimensionCriterion;
import com.evinceframework.data.warehouse.query.criterion.Expression;
import com.evinceframework.data.warehouse.query.criterion.FactRangeCriterion;
import com.evinceframework.data.warehouse.query.criterion.LogicalExpression;
import com.evinceframework.data.warehouse.query.jdbc.SqlQueryCriteriaBuilder.CriterionProcessor;

public class SqlQueryCriteriaBuilder 
		extends AbstractClassLookupFactory<CriterionProcessor<? extends Expression>> {

	private ParameterValueSetterFactory parameterSupport = ParameterValueSetterFactory.DEFAULT_FACTORY;
	
	private List<CriterionProcessor<?>> delegates = new ArrayList<CriterionProcessor<?>>();
	
	
	public SqlQueryCriteriaBuilder() {
		delegates.add(new DimensionCriterionProcessor());
		delegates.add(new FactRangeCriterionProcessor());
	}

	@Override
	protected CriterionProcessor<? extends Expression> create(Class<?> clazz) {
		
		if(clazz != null) {
			for(CriterionProcessor<?> delegate : delegates) {
				if(clazz.equals(delegate.getCriterionClass())) {
					return delegate;
				}
			}
		}
		return null;
	}
	
	public String createWhereClause(SqlQueryBuilder sqlBuilder, Expression[] criteria) {
		
		if(criteria == null)
			return null;
		
		List<String> clauses = new ArrayList<String>();
		
		for(Expression c : criteria) {
			CriterionProcessor<?> processor = lookup(c.getClass());
			String clause = processor.processCriterion(sqlBuilder, c);
			if(clause != null)
				clauses.add(clause);	
		}
		
		return joinWhereClauses(clauses);
	}
	
	public int setParameters(SqlQueryBuilder sqlBuilder, Expression[] criteria, PreparedStatement stmt, int paramIdx) 
			throws SQLException, QueryException {
		
		int idx = paramIdx;
		
		for(Expression criterion : criteria) {
			CriterionProcessor<?> processor = lookup(criterion.getClass());
			idx += processor.setCriterionParameters(sqlBuilder, criterion, stmt, idx);
		}
		
		return idx;
	}
	
	public String joinWhereClauses(List<String> clauses) {
		return joinClauses(clauses, LogicalExpression.And.OPERATOR);
	}
	
	protected String joinClauses(List<String> clauses, String logicalOperator) {
		
		if(clauses.size() == 0)
			return null;
		
		if(clauses.size() == 1)
			return clauses.get(0);
		
		return String.format("(%s)", StringHelper.join(
				String.format(" %s ", logicalOperator), clauses.toArray(new String[]{})));
	}
	
	public String createSingularWhereClause(String alias, String column) {
		return createSingularWhereClause(alias, column, "=");
	}
	
	public String createSingularWhereClause(String alias, String column, String comparisonOperator) {
		return String.format("%s.%s %s ?", alias, column, comparisonOperator);
	}
	
	public String createWhereInClause(String alias, String column, int parameterCount) {
		
		if(parameterCount <= 0)
			return "";
		
		if(parameterCount == 1)
			return createSingularWhereClause(alias, column);
		
		String[] marks = new String[parameterCount];
		for(int i = 0; i<parameterCount; i++){
			marks[i] = "?";
		}
		
		return String.format("%s.%s in (%s)", alias, column, StringHelper.join(",", marks));
	}

	private static String getSqlComparison(ComparisonOperator operator) {
		
		return "";
	}
	
	public static abstract class CriterionProcessor<T extends Expression> {
		
		public abstract Class<?> getCriterionClass();
		
		@SuppressWarnings("unchecked")
		public String processCriterion(SqlQueryBuilder sqlBuilder, Expression criterion) {
			return onProcess(sqlBuilder, (T) criterion);
		}
		
		@SuppressWarnings("unchecked")
		public int setCriterionParameters(SqlQueryBuilder sqlBuilder, Expression criterion, PreparedStatement stmt, int paramIdx) 
				throws SQLException, QueryException {
			return onSetParameters(sqlBuilder, (T) criterion, stmt, paramIdx);
		}
		
		public abstract String onProcess(SqlQueryBuilder sqlBuilder, T criterion);
		
		public abstract int onSetParameters(SqlQueryBuilder sqlBuilder, T criterion, PreparedStatement stmt, int paramIdx) 
				throws SQLException, QueryException;
	}
	
	public class DimensionCriterionProcessor extends CriterionProcessor<DimensionCriterion<?>> {

		@Override
		public Class<?> getCriterionClass() {
			return DimensionCriterion.class;
		}
		
		@Override
		public String onProcess(SqlQueryBuilder sqlBuilder, DimensionCriterion<?> dc) {
			return createWhereInClause(sqlBuilder.joinDimension(dc.getDimension()), 
					dc.getDimensionalAttribute().getColumnName(), dc.getValues().length);
		}
		
		@Override
		public int onSetParameters(SqlQueryBuilder sqlBuilder, DimensionCriterion<?> criterion, PreparedStatement stmt, int paramIdx) 
				throws SQLException, QueryException {
			
			return parameterSupport.setParameterValues(criterion.getDimensionalAttribute().getValueType(),
					stmt, criterion.getValues(), paramIdx);
		}
	}
	
	public class FactRangeCriterionProcessor extends CriterionProcessor<FactRangeCriterion<?>> {

		@Override
		public Class<?> getCriterionClass() {
			return FactRangeCriterion.class;
		}
		
		@Override
		public String onProcess(SqlQueryBuilder sqlBuilder, FactRangeCriterion<?> frc) {
			return createSingularWhereClause(
					sqlBuilder.getFactTableAlias(), frc.getFact().getColumnName(),
					getSqlComparison(frc.getOperator()));
		}
		
		@Override
		public int onSetParameters(SqlQueryBuilder sqlBuilder, FactRangeCriterion<?> frc, PreparedStatement stmt, int paramIdx) 
				throws SQLException, QueryException {
			
			return parameterSupport.setParameterValue(
					frc.getFact().getValueType(), stmt, frc.getValue(), paramIdx);
		}
	}
	
	public class LogicalExpressionProcessor extends CriterionProcessor<LogicalExpression> {

		@Override
		public Class<?> getCriterionClass() {
			return LogicalExpression.class;
		}

		@Override
		public String onProcess(SqlQueryBuilder sqlBuilder, LogicalExpression criterion) {
			
			List<String> clauses = new ArrayList<String>(); 
			clauses.add(processCriterion(sqlBuilder, criterion.getLeftHandExpression()));
			clauses.add(processCriterion(sqlBuilder, criterion.getRightHandExpression()));
			return joinClauses(clauses, criterion.getExpression());
		}

		@Override
		public int onSetParameters(SqlQueryBuilder sqlBuilder, LogicalExpression criterion,
				PreparedStatement stmt, int paramIdx) throws SQLException, QueryException {
			
			int pi = paramIdx;
			
			pi += setParameters(sqlBuilder, new Expression[] {
				criterion.getLeftHandExpression(),
				criterion.getRightHandExpression()
			}, stmt, paramIdx);
			
			return pi;
				
		}
	}

}
