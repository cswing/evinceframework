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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.dialect.Dialect;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.evinceframework.data.warehouse.query.PivotQuery;
import com.evinceframework.data.warehouse.query.PivotQueryResult;
import com.evinceframework.data.warehouse.query.QueryException;
import com.evinceframework.data.warehouse.query.SummarizationAttribute;

public class PivotJdbcQueryCommand extends AbstractJdbcQueryCommand<PivotQuery, PivotQueryResult> {

	private static final Log logger = LogFactory.getLog(PivotJdbcQueryCommand.class);
	
	public static final int DEFAULT_ROW_LIMIT = 10000;
	
	private MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(new QueryEngineMessageSource());
	
	private Integer rowLimit = null;
	
	public PivotJdbcQueryCommand(JdbcTemplate jdbcTemplate, Dialect dialect) {
		this(jdbcTemplate, dialect, DEFAULT_ROW_LIMIT);
	}

	public PivotJdbcQueryCommand(JdbcTemplate jdbcTemplate, Dialect dialect, Integer rowLimit) {
		super(jdbcTemplate, dialect);
		this.rowLimit = rowLimit;
	}
	
	public Integer getRowLimit() {
		return rowLimit;
	}

	@Override
	protected PivotQueryResult createResult(PivotQuery query) {
		return null;
	}

	@Override
	protected PreparedStatementCreator createCreator(final PivotQuery query, final PivotQueryResult result) {
		
		return new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			
				SqlQueryBuilder.SqlStatementText sqlResult = null;
				
				try {
					sqlResult = generateSql(query, result);
					
				} catch (QueryException e) {
					throw new SQLException(e);
				}
				
				PreparedStatement stmt = con.prepareStatement(sqlResult.sql);
				
				int paramIdx = 0;
				
				if(sqlResult.limitHandler != null)
					paramIdx += sqlResult.limitHandler.bindLimitParametersAtStartOfQuery(stmt, paramIdx);
				
				// set parameters
	//			for(DimensionCriterion dc : query.getDimensionCriterion()) {
	//				paramIdx += dc.setParameterValue(stmt, paramIdx);
	//			}
				
				if(sqlResult.limitHandler != null)
					paramIdx += sqlResult.limitHandler.bindLimitParametersAtEndOfQuery(stmt, paramIdx);
				
				return stmt;
			}	
		};
	}

	protected SqlQueryBuilder.SqlStatementText generateSql(PivotQuery query, PivotQueryResult result) throws QueryException {
		
		/*
		 A pivot table usually consists of row, column and data (or fact) fields. In this case, the column is 
		 Ship Date, the row is Region and the datum we would like to see is (sum of) Units. These fields allow 
		 several kinds of aggregations including: sum, average, standard deviation, count etc. In this case, the 
		 total number of units shipped is displayed here using a sum aggregation.
		 */
		
		if(query.getFactSelections() == null || query.getFactSelections().length == 0)
			throw new QueryException(messageSourceAccessor.getMessage(
					QueryEngineMessageSource.MISSING_FACT_SELECTION, LocaleContextHolder.getLocale()));
		
		SqlQueryBuilder builder = new SqlQueryBuilder(query, getDialect());
		
		// Summarization columns
		SummarizationAttribute[] summaryAttributes = query.getSummarizations();
		if(summaryAttributes != null && summaryAttributes.length > 0) {
			
			for(SummarizationAttribute attr : summaryAttributes) {
				
				builder.joinDimension(attr.getDimension());
				
				// TODO reimplement and test
//				for(DimensionalAttribute<? extends Object> dimAttr : attr.getDimension().getDimensionTable().getBusinessKey()) {
//					selectFrag.addColumn(alias, dimAttr.getColumnName());
//					groupBy.add(StringHelper.qualify(alias, dimAttr.getColumnName()));
//				}
				
				// TODO support grouping by a dimensional attribute and not just a dimension
			}
		}
		
		
		builder.addFactSelections(query.getFactSelections());
		builder.processDimensionCriterion(query);
		builder.processFactRangeCriterion(query); // TODO write tests for this line
		
		Integer limit = null;
		
		if(query.getMaximumRowCount() != null || this.rowLimit != null) {
			
			limit = query.getMaximumRowCount();
			
			if(limit == null) {
				limit = this.rowLimit;
				
			} else if(this.rowLimit != null && limit > this.rowLimit){
				result.getMessages().add(messageSourceAccessor.getMessage(
						QueryEngineMessageSource.ROW_LIMIT_EXCEEDED, new Object[] { this.rowLimit }, LocaleContextHolder.getLocale()));
				
				limit = this.rowLimit;
			}
		}
		
		SqlQueryBuilder.SqlStatementText sqlResult = builder.generateSqlText(limit);
		
		if(limit == null)
			return sqlResult; 
			
		if(!sqlResult.limitHandler.supportsLimit()) {
			 
			if(this.rowLimit != null) {
				logger.warn(String.format(
						"The query engine is configured to limit the number of rows but the underlying database dialect [%s] does not support this.",
						getDialect().toString()));
			}
			
			if(query.getMaximumRowCount() != null) {
				result.getMessages().add(messageSourceAccessor.getMessage(
						QueryEngineMessageSource.ROW_LIMIT_NOT_SUPPORTED, LocaleContextHolder.getLocale()));
			}
		}

		return sqlResult;		
	}
	
	@Override
	protected ResultSetExtractor<PivotQueryResult> createExtractor(final PivotQuery query, final PivotQueryResult result) {
		
		return new ResultSetExtractor<PivotQueryResult>() {
			@Override
			public PivotQueryResult extractData(ResultSet rs) throws SQLException, DataAccessException {
				
				
				return result;
			}
		};
	}
	
}
