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

import java.util.ArrayList;
import java.util.List;

import com.evinceframework.core.factory.AbstractClassLookupFactory;
import com.evinceframework.core.factory.LookupFactory;
import com.evinceframework.data.warehouse.query.jdbc.SqlQueryBuilder;

public class SqlCriterionContext {

	private SqlQueryBuilder queryBuilder;
	
	private static final LookupFactory<Class<?>, SqlCriterion> criterionFactory = new CriterionLookupFactory();
		
	public SqlCriterionContext(SqlQueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	public LookupFactory<Class<?>, SqlCriterion> getCriterionFactory() {
		return criterionFactory;
	}

	public SqlQueryBuilder getQueryBuilder() {
		return queryBuilder;
	}
	
	private static class CriterionLookupFactory extends AbstractClassLookupFactory<SqlCriterion> {

		private List<SqlCriterion> delegates = new ArrayList<SqlCriterion>();
		
		public CriterionLookupFactory() {
			delegates.add(new DimensionSqlCriterion());
			delegates.add(new LogicalSqlCriterion());
			delegates.add(new FactSqlCriterion());
		}

		@Override
		protected SqlCriterion create(Class<?> clazz) {
			
			if(clazz != null) {
				for(SqlCriterion sc : delegates) {
					if(clazz.equals(sc.getCriterionClass()))
						return sc;
				}
			}
			
			return null;
		}
		
	}
}
