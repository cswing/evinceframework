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

import org.hibernate.dialect.Dialect;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.evinceframework.data.warehouse.query.DimensionCriterion;
import com.evinceframework.data.warehouse.query.DrillPathEntry;
import com.evinceframework.data.warehouse.query.DrillPathValue;
import com.evinceframework.data.warehouse.query.FactRangeCriterion;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.HierarchicalQuery;
import com.evinceframework.data.warehouse.query.HierarchicalQueryResult;
import com.evinceframework.data.warehouse.query.QueryException;

public class HierarchicalJdbcQueryCommand extends AbstractJdbcQueryCommand<HierarchicalQuery, HierarchicalQueryResult> {
	
	private ParameterValueSetterFactory parameterSupport = ParameterValueSetterFactory.DEFAULT_FACTORY;
	
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
				
				try {
				
					String sql = generateSql(query, result);
					PreparedStatement stmt = con.prepareStatement(sql);
					int paramIdx = 0;
					
					DrillPathValue<?> qRoot = query.getQueryRoot(); 
					
					if(query.getQueryRoot() != null) {
						// Add dimension/attribute value
						paramIdx += parameterSupport.setParameterValue(
								qRoot.getEntry().getDimensionalAttribute().getValueType(), 
								stmt, qRoot.getValue(), paramIdx);
					}
					
					// dimension criterion
					for(DimensionCriterion<?> dc : query.getDimensionCriterion()) {
						paramIdx += parameterSupport.setParameterValues(dc.getDimensionalAttribute().getValueType(),
								stmt, dc.getValues(), paramIdx);
					}
					
					// fact criterion
					for(FactRangeCriterion frc : query.getFactCriterion()) {
						if (frc.getLowerBound() != null) {
							paramIdx += parameterSupport.setParameterValue(
									frc.getFact().getValueType(), stmt, frc.getLowerBound(), paramIdx);
						}
					}
					
					return stmt;
					
				} catch (QueryException e) {
					throw new SQLException(e);
				}
			}
		};
	}

	protected String generateSql(final HierarchicalQuery query, final HierarchicalQueryResult result) 
			throws QueryException {
		
		if(query.getFactSelections().length == 0) {
			// TODO add message or throw
			//return;
		}
		
		if(query.getFactSelections().length > 1) {
			// TODO add message or throw
			//return;
		}
		
		FactSelection fact = query.getFactSelections()[0];
		if(fact.getFunction() == null) {
			// TODO throw
		}
		
		SqlQueryBuilder sqlBuilder = new SqlQueryBuilder(query, getDialect());
		
		sqlBuilder.addFactSelection(fact);
		
		if(query.getQueryRoot() == null) {
			sqlBuilder.processDrillPath(query.getRoot(), query.getLevels());
			
		} else {
			
			DrillPathEntry<?> qRoot = query.getQueryRoot().getEntry();
			
			// If a query root is provided then filter based on the that and get the next X levels  
			sqlBuilder.processDrillPath(qRoot.next(), query.getLevels());
			sqlBuilder.addFilter(qRoot.getDimension(), qRoot.getDimensionalAttribute());
		}
		
		sqlBuilder.processDimensionCriterion(query);
		sqlBuilder.processFactRangeCriterion(query);
		
		return sqlBuilder.generateSqlText().sql;
	}
	
	@Override
	protected ResultSetExtractor<HierarchicalQueryResult> createExtractor(
			final HierarchicalQuery query, final HierarchicalQueryResult result) {
		
		return new ResultSetExtractor<HierarchicalQueryResult>() {
			@Override
			public HierarchicalQueryResult extractData(ResultSet rs) throws SQLException, DataAccessException {
				
				while(rs.next()) {
					
					
					
				}
				
				/*
				
				DrillPathEntry: {
					dimension
					attribute
					next()
				}
				
				DrillPathValue: {
					drillPathEntry
					value
				}
				
				HierarchicalDataEntry: {
					value: 				23
					DrillPathEntry: 	
				}
				 
				 var item = data[idx++];
			if(!item) return;

			// create root customer node
			var cNode = customerMap[item.customerId]
			if (!cNode) {
				cNode = customerMap[item.customerId] = { id: item.customerId, r: 6, tooltip: fnCompanyTooltip, _item: item };
				nodes.push(cNode);
				links.push({source: rootNode, target: cNode});
			}

			var hNode = { id: 'hours hours_' + counter ++, r: item.hours/2.0 };
			nodes.push(hNode);
			links.push({source: cNode, target: hNode});


			//setTimeout(fnProcessIndex, 10);
			fnProcessIndex(); 
				 */
				return result;
			}
		};
	}

}
