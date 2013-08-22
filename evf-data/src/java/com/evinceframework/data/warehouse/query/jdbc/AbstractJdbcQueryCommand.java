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

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.evinceframework.data.warehouse.query.Query;
import com.evinceframework.data.warehouse.query.QueryCommand;
import com.evinceframework.data.warehouse.query.QueryResult;

public abstract class AbstractJdbcQueryCommand<Q extends Query, R extends QueryResult>
		implements QueryCommand<Q,R> {

	private Class<Q> queryType; 
	
	private Class<R> resultType;
	
	private JdbcTemplate jdbcTemplate;
	
	private Dialect dialect;
	
	public AbstractJdbcQueryCommand(Class<Q> queryType, Class<R> resultType, DataSource dataSource, Dialect dialect) {
		this.queryType = queryType;
		this.resultType = resultType;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dialect = dialect;
	}

	@Override
	public Class<Q> getQueryType() {
		return queryType;
	}

	@Override
	public Class<R> getResultType() {
		return resultType;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public Dialect getDialect() {
		return dialect;
	}

	protected abstract R createResult(Q query);
	
	protected abstract PreparedStatementCreator createCreator(Q query, R result, SqlQueryBuilder sqlBuilder);
	
	protected abstract ResultSetExtractor<R> createExtractor(Q query, R result, SqlQueryBuilder sqlBuilder);
	
	@Override
	public R query(Q query) {
		
		R result = createResult(query);
		
		SqlQueryBuilder sqlBuilder = new SqlQueryBuilder(query, getDialect());
		
		return (R) getJdbcTemplate().query(
				createCreator(query, result, sqlBuilder), createExtractor(query, result, sqlBuilder));
	}
		
}
