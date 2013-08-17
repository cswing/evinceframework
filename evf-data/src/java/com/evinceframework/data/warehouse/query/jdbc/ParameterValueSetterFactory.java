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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.evinceframework.core.factory.MapBackedClassLookupFactory;
import com.evinceframework.data.warehouse.query.QueryException;

public class ParameterValueSetterFactory extends MapBackedClassLookupFactory<ParameterValueSetter> {
	
	public static final ParameterValueSetterFactory DEFAULT_FACTORY = new ParameterValueSetterFactory();
	
	public ParameterValueSetterFactory() {
		getLookupMap().put(BigDecimal.class, new BigDecimalSetter());
		getLookupMap().put(Boolean.class, new BooleanSetter());
		getLookupMap().put(Date.class, new DateSetter());
		getLookupMap().put(Integer.class, new IntegerSetter());
		getLookupMap().put(String.class, new StringSetter());
	}

	public int setParameterValue(Class<?> type, PreparedStatement stmt, Object value, int paramIdx) 
			throws SQLException, QueryException {
		return setParameterValues(type, stmt, new Object[]{ value }, paramIdx);
	}
	
	public int setParameterValues(Class<?> type, PreparedStatement stmt, Object[] values, int paramIdx) 
			throws SQLException, QueryException {
		
		ParameterValueSetter setter = lookup(type);
		if(setter == null)
			throw new QueryException("Unknown type " + type.getName()); // TODO i18n
		
		return setter.setParameterValues(stmt, values, paramIdx);
	}
	
	private static class BigDecimalSetter extends AbstractParameterValueSetter<BigDecimal> {
		@Override
		protected int setParameterValue(PreparedStatement stmt, BigDecimal value, int paramIdx) throws SQLException {
			stmt.setBigDecimal(paramIdx, value);
			return paramIdx + 1;
		}
	}
		
	private static class BooleanSetter extends AbstractParameterValueSetter<Boolean> {
		@Override
		protected int setParameterValue(PreparedStatement stmt, Boolean value, int paramIdx) throws SQLException {
			stmt.setBoolean(paramIdx, value);
			return paramIdx + 1;
		}
	}
	
	private static class DateSetter extends AbstractParameterValueSetter<Date> {
		@Override
		protected int setParameterValue(PreparedStatement stmt, Date value, int paramIdx) throws SQLException {
			stmt.setObject(paramIdx, value);
			return paramIdx + 1;
		}
	}
	
	private static class IntegerSetter extends AbstractParameterValueSetter<Integer> {
		@Override
		protected int setParameterValue(PreparedStatement stmt, Integer value, int paramIdx) throws SQLException {
			stmt.setInt(paramIdx, value);
			return paramIdx + 1;
		}
	}
	
	private static class StringSetter extends AbstractParameterValueSetter<String> {
		@Override
		protected int setParameterValue(PreparedStatement stmt, String value, int paramIdx) throws SQLException {
			stmt.setString(paramIdx, value);
			return paramIdx + 1;
		}
	}
}
