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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.dialect.Dialect;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.Select;
import org.hibernate.sql.SelectFragment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.evinceframework.data.warehouse.FactTable;
import com.evinceframework.data.warehouse.query.HierarchicalQuery;
import com.evinceframework.data.warehouse.query.HierarchicalQueryResult;
import com.evinceframework.data.warehouse.query.QueryException;

public class HierarchicalJdbcQueryCommand extends AbstractJdbcQueryCommand<HierarchicalQuery, HierarchicalQueryResult> {
	
	public HierarchicalJdbcQueryCommand(JdbcTemplate jdbcTemplate, Dialect dialect) {
		super(jdbcTemplate, dialect);
	}

	@Override
	protected HierarchicalQueryResult createResult(HierarchicalQuery query) {
		return new HierarchicalQueryResult(query);
	}

	@Override
	protected PreparedStatementCreator createCreator(
			final HierarchicalQuery query, final HierarchicalQueryResult result) {
		
		return new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				
				SqlGenerationResult sqlResult = null;
				
				try {
					sqlResult = generateSql(query, result);
					
				} catch (QueryException e) {
					throw new SQLException(e);
				}
				
				PreparedStatement stmt = con.prepareStatement(sqlResult.sql);
				
				int paramIdx = 0;
				
//				if(sqlResult.limitHandler != null)
//					paramIdx += sqlResult.limitHandler.bindLimitParametersAtStartOfQuery(stmt, paramIdx);
//				
//				// set parameters
//				for(DimensionCriterion dc : query.getDimensionCriterion()) {
//					paramIdx += dc.setParameterValue(stmt, paramIdx);
//				}
//				
//				if(sqlResult.limitHandler != null)
//					paramIdx += sqlResult.limitHandler.bindLimitParametersAtEndOfQuery(stmt, paramIdx);
//				
				return stmt;
			}
		};
	}

	protected SqlGenerationResult generateSql(
			final HierarchicalQuery query, final HierarchicalQueryResult result) throws QueryException {
		
		SqlGenerationResult sqlResult = new SqlGenerationResult();
		
		FactTable fact = query.getFactTable();
		String factTableAlias = "fact";
		
		Select select = new Select(getDialect());
		select.setFromClause(fact.getTableName(), factTableAlias);
		SelectFragment selectFrag = new SelectFragment();
		
		JoinFragment joinFrag = getDialect().createOuterJoinFragment();
		List<String> groupBy = new LinkedList<String>();
		List<String> where = new LinkedList<String>();

		// For each drill path entry
		// - join dimension table if not already joined
		// - add to select clause
		// - add to group by clause
		
		// If the query root is not the root of the drill path, 
		//  then add to the where clause based on the root of the 
		//  query
		
		
		// Update select
		select.setSelectClause(selectFrag);
		select.setOuterJoins(joinFrag.toFromFragmentString(), joinFrag.toWhereFragmentString());
		
		sqlResult.sql = select.toStatementString();
		
		return sqlResult;
	}
	
	@Override
	protected ResultSetExtractor<HierarchicalQueryResult> createExtractor(
			final HierarchicalQuery query, final HierarchicalQueryResult result) {
		
		return new ResultSetExtractor<HierarchicalQueryResult>() {
			@Override
			public HierarchicalQueryResult extractData(ResultSet rs) throws SQLException, DataAccessException {
				
				
				return result;
			}
		};
	}

}
