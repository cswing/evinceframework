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
import com.evinceframework.data.warehouse.query.criterion.Criterion;
import com.evinceframework.data.warehouse.query.criterion.DimensionCriterion;
import com.evinceframework.data.warehouse.query.criterion.FactRangeCriterion;
import com.evinceframework.data.warehouse.query.jdbc.SqlQueryCriteriaBuilder.CriterionProcessor;

public class SqlQueryCriteriaBuilder 
		extends AbstractClassLookupFactory<CriterionProcessor<? extends Criterion>> {

	private ParameterValueSetterFactory parameterSupport = ParameterValueSetterFactory.DEFAULT_FACTORY;
	
	private List<CriterionProcessor<?>> delegates = new ArrayList<CriterionProcessor<?>>();
	
	
	public SqlQueryCriteriaBuilder() {
		delegates.add(new DimensionCriterionProcessor());
		delegates.add(new FactRangeCriterionProcessor());
	}

	@Override
	protected CriterionProcessor<? extends Criterion> create(Class<?> clazz) {
		
		if(clazz != null) {
			for(CriterionProcessor<?> delegate : delegates) {
				if(clazz.equals(delegate.getCriterionClass())) {
					return delegate;
				}
			}
		}
		return null;
	}
	
	public void processCriterion(SqlQueryBuilder sqlBuilder, Criterion criterion) {
		
		if(criterion == null)
			return;
		
		CriterionProcessor<?> processor = lookup(criterion.getClass());
		processor.process(sqlBuilder, criterion);
	}
	
	public int setParameters(SqlQueryBuilder sqlBuilder, Criterion[] criteria, PreparedStatement stmt, int paramIdx) 
			throws SQLException, QueryException {
		
		int idx = paramIdx;
		
		for(Criterion criterion : criteria) {
			CriterionProcessor<?> processor = lookup(criterion.getClass());
			idx += processor.setParameters(sqlBuilder, criterion, stmt, idx);
		}
		
		return idx;
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

	public static abstract class CriterionProcessor<T extends Criterion> {
		
		public abstract Class<?> getCriterionClass();
		
		@SuppressWarnings("unchecked")
		public void process(SqlQueryBuilder sqlBuilder, Criterion criterion) {
			onProcess(sqlBuilder, (T) criterion);
		}
		
		@SuppressWarnings("unchecked")
		public int setParameters(SqlQueryBuilder sqlBuilder, Criterion criterion, PreparedStatement stmt, int paramIdx) 
				throws SQLException, QueryException {
			return onSetParameters(sqlBuilder, (T) criterion, stmt, paramIdx);
		}
		
		public abstract void onProcess(SqlQueryBuilder sqlBuilder, T criterion);
		
		public abstract int onSetParameters(SqlQueryBuilder sqlBuilder, T criterion, PreparedStatement stmt, int paramIdx) 
				throws SQLException, QueryException;
	}
	
	public class DimensionCriterionProcessor extends CriterionProcessor<DimensionCriterion<?>> {

		@Override
		public Class<?> getCriterionClass() {
			return DimensionCriterion.class;
		}
		
		@Override
		public void onProcess(SqlQueryBuilder sqlBuilder, DimensionCriterion<?> dc) {
			sqlBuilder.where.add(
					createWhereInClause(sqlBuilder.joinDimension(dc.getDimension()), 
							dc.getDimensionalAttribute().getColumnName(), dc.getValues().length));
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
		public void onProcess(SqlQueryBuilder sqlBuilder, FactRangeCriterion<?> frc) {

			if (frc.getLowerBound() != null) {
				sqlBuilder.where.add(createSingularWhereClause(
						sqlBuilder.getFactTableAlias(), frc.getFact().getColumnName(), frc.isLowerBoundInclusive() ? ">=" : ">"));
			}
			
			if (frc.getUpperBound() != null) {
				sqlBuilder.where.add(createSingularWhereClause(
						sqlBuilder.getFactTableAlias(), frc.getFact().getColumnName(), frc.isUpperBoundInclusive() ? "<=" : "<"));
			}
		}
		
		@Override
		public int onSetParameters(SqlQueryBuilder sqlBuilder, FactRangeCriterion<?> frc, PreparedStatement stmt, int paramIdx) 
				throws SQLException, QueryException {
			
			if (frc.getLowerBound() != null) {
				paramIdx += parameterSupport.setParameterValue(
						frc.getFact().getValueType(), stmt, frc.getLowerBound(), paramIdx);
			}

			if (frc.getUpperBound() != null) {
				paramIdx += parameterSupport.setParameterValue(
						frc.getFact().getValueType(), stmt, frc.getUpperBound(), paramIdx);
			}
			
			return 0;
		}
	}
}
