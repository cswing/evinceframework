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

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.pagination.LimitHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.evinceframework.data.warehouse.query.Query;
import com.evinceframework.data.warehouse.query.QueryCommand;
import com.evinceframework.data.warehouse.query.QueryResult;

public abstract class AbstractJdbcQueryCommand<Q extends Query, R extends QueryResult>
		implements QueryCommand<Q,R> {

	private JdbcTemplate jdbcTemplate;
	
	private Dialect dialect;
	
	public AbstractJdbcQueryCommand(JdbcTemplate jdbcTemplate, Dialect dialect) {
		this.jdbcTemplate = jdbcTemplate;
		this.dialect = dialect;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public Dialect getDialect() {
		return dialect;
	}

	protected abstract R createResult(Q query);
	
	protected abstract PreparedStatementCreator createCreator(Q query, R result);
	
	protected abstract ResultSetExtractor<R> createExtractor(Q query, R result);
	
	@Override
	public R query(Q query) {
		
		R result = createResult(query);
		
		return (R) getJdbcTemplate().query(
				createCreator(query, result), createExtractor(query, result));
	}
		
}
