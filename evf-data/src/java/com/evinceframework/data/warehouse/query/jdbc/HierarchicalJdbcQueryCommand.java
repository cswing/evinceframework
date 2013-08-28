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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.evinceframework.data.warehouse.query.DrillPathData;
import com.evinceframework.data.warehouse.query.DrillPathEntry;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.HierarchicalQuery;
import com.evinceframework.data.warehouse.query.HierarchicalQueryResult;
import com.evinceframework.data.warehouse.query.QueryException;

public class HierarchicalJdbcQueryCommand extends AbstractJdbcQueryCommand<HierarchicalQuery, HierarchicalQueryResult> {
	
	private ParameterValueSetterFactory parameterSupport = ParameterValueSetterFactory.DEFAULT_FACTORY;
	
	public HierarchicalJdbcQueryCommand(DataSource dataSource, Dialect dialect) {
		super(HierarchicalQuery.class, HierarchicalQueryResult.class, dataSource, dialect);
	}
	
	@Override
	protected HierarchicalQueryResult createResult(HierarchicalQuery query) {
		return new HierarchicalQueryResult(query);
	}

	@Override
	protected PreparedStatementCreator createCreator(
			final HierarchicalQuery query, final HierarchicalQueryResult result, final SqlQueryBuilder sqlBuilder) {
		
		return new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				
				try {
				
					String sql = generateSql(query, result, sqlBuilder);
					PreparedStatement stmt = con.prepareStatement(sql);
					int paramIdx = 0;
					
					DrillPathEntry<?> entry = query.getQueryRoot();
					
					// filters from selected drill path
					while(entry != null) {
						paramIdx += parameterSupport.setParameterValue(
								entry.getDimensionalAttribute().getValueType(), 
								stmt, entry.getFilterValue(), paramIdx);
					}
					
					// criterion
					paramIdx += sqlBuilder.getCriteriaBuilder()
							.setParameters(sqlBuilder, query.getCriteria(), stmt, paramIdx);
					
					return stmt;
					
				} catch (QueryException e) {
					throw new SQLException(e);
				}
			}
		};
	}

	protected String generateSql(final HierarchicalQuery query, final HierarchicalQueryResult result, 
			final SqlQueryBuilder sqlBuilder) throws QueryException {
		
		if(query.getFactSelections() == null || query.getFactSelections().length == 0)
			throw new QueryException(messageSourceAccessor.getMessage(
					QueryEngineMessageSource.MISSING_FACT_SELECTION, LocaleContextHolder.getLocale()));
			
		if(query.getFactSelections().length > 1)
			throw new QueryException(messageSourceAccessor.getMessage(
					QueryEngineMessageSource.ONLY_SINGLE_FACT_SELECTION_SUPPORTED, LocaleContextHolder.getLocale()));
		
		FactSelection fact = query.getFactSelections()[0];
		if(fact.getFunction() == null)
			throw new QueryException(messageSourceAccessor.getMessage(
					QueryEngineMessageSource.FACT_SELECTION_REQUIRES_FUNCTION, LocaleContextHolder.getLocale()));
		
		sqlBuilder.addFactSelection(fact);
		
		if(query.getQueryRoot() == null) {
			sqlBuilder.processDrillPath(query.getRoot(), query.getLevels());
			
		} else {
			
			DrillPathEntry<?> entry = query.getQueryRoot();
			
			// If a query root is provided then filter based on the that and get the next X levels  
			sqlBuilder.processDrillPath(entry.getNextEntry(), query.getLevels());
			while(entry != null) {
				sqlBuilder.addFilter(entry.getDimension(), entry.getDimensionalAttribute());
				entry = entry.getPreviousEntry();
			}
		}
		
		sqlBuilder.processCriteria(query);
		
		return sqlBuilder.generateSqlText().sql;
	}
	
	@Override
	protected ResultSetExtractor<HierarchicalQueryResult> createExtractor(
			final HierarchicalQuery query, final HierarchicalQueryResult result, final SqlQueryBuilder builder) {
		
		return new ResultSetExtractor<HierarchicalQueryResult>() {
			@Override
			public HierarchicalQueryResult extractData(ResultSet rs) throws SQLException, DataAccessException {
				
				Map<String, DrillPathData<BigDecimal>> drillPathMap = new HashMap<String, DrillPathData<BigDecimal>>(); 
				
				DrillPathEntry<?> root = query.getRoot();
				if(query.getQueryRoot() != null) {
					root = query.getQueryRoot().getNextEntry();
				}
				
				while(rs.next()) {
					int i = 0;
					DrillPathEntry<?> entry = root;
					DrillPathData<BigDecimal> parentEntry = null;
					while(entry != null && i++ < query.getLevels()) {
						DrillPathData<BigDecimal> data = findOrCreateDrillPathData(entry, rs, drillPathMap, builder, parentEntry);
						BigDecimal value = rs.getBigDecimal(builder.lookupAlias(query.getFactSelections()[0]));
						data.setValue(data.getValue().add(value));
						
						parentEntry = data;
						entry = entry.getNextEntry();
					}
				}
				
				return result;
			}
		};
	}

	protected DrillPathData<BigDecimal> findOrCreateDrillPathData(DrillPathEntry<?> main, ResultSet rs, 
			Map<String, DrillPathData<BigDecimal>> drillPathMap, SqlQueryBuilder builder, 
			DrillPathData<BigDecimal> parent) throws SQLException {
		
		List<String> keys = new ArrayList<String>();
		DrillPathEntry<?> entry = main.getRootEntry();
		while(entry != null) {
			keys.add(String.format("%s.%s[%s]",	
					entry.getDimension().getForeignKeyColumn(),
					entry.getDimensionalAttribute().getColumnName(),
					rs.getObject(builder.lookupAlias(entry.getDimension(), entry.getDimensionalAttribute()))
			));
			
			if(entry == main) {
				entry = null;
			} else {
				entry = entry.getNextEntry();
			}
		}
		String key = StringHelper.join("::", keys.iterator());
		
		DrillPathData<BigDecimal> data = drillPathMap.get(key);
		if(data == null) {
			data = new DrillPathData<BigDecimal>(parent, main, key);
			drillPathMap.put(key, data);
		}
			
		return data;
	}

}
