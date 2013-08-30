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

import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.data.warehouse.query.criterion.FactRangeCriterion;

public class FactSqlCriterion extends AbstractSqlCriterion<FactRangeCriterion<?>> {

	@Override
	public Class<?> getCriterionClass() {
		return FactRangeCriterion.class;
	}

	@Override
	public String onCreateWhereClause(SqlCriterionContext context, FactRangeCriterion<?> criterion) {
		return createSingularWhereClause(
				context.getQueryBuilder().getFactTableAlias(), criterion.getFact().getColumnName(),
				getSqlComparison(criterion.getOperator()));
	}

	@Override
	public int onSetParameters(SqlCriterionContext context, FactRangeCriterion<?> criterion,
			PreparedStatement stmt, int paramIdx) throws SQLException, QueryException {
		
		return parameterSupport.setParameterValue(
				criterion.getFact().getValueType(), stmt, criterion.getValue(), paramIdx);
	}

}
