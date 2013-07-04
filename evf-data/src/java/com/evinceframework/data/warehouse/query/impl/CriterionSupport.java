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
package com.evinceframework.data.warehouse.query.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class CriterionSupport<T> {
	
	private static final Map<Class<?>, InternalParameterValueSetter<?>> setters = 
			new HashMap<Class<?>, InternalParameterValueSetter<?>>();
	
	static {
		setters.put(BigDecimal.class, new BigDecimalSetter());
		setters.put(Boolean.class, new BooleanSetter());
		setters.put(Date.class, new DateSetter());
		setters.put(Integer.class, new IntegerSetter());
		setters.put(String.class, new StringSetter());
	}
	
	private Class<T> baseClass;
	
	private T[] values;
	
	protected CriterionSupport(Class<T> baseClass, T[] values) {
		
		assert(values != null);
		assert(values.length > 0);
		assert(setters.containsKey(baseClass));
		
		this.baseClass = baseClass;
		this.values = values;
	}

	protected String createWhereFormat() {
		
		if (values.length == 1) {
			return "%s = ?";
		}
		
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i<values.length; i++){
			builder.append("?");
			
			if(i < values.length - 1)
				builder.append(",");
		}
		
		return String.format("%s in (%s)", "%s", builder);
	}
	
	public int setParameterValue(PreparedStatement stmt, int paramIdx)  throws SQLException {
		return setters.get(baseClass).setParameterValues(stmt, values, paramIdx);
	}
	
	private static abstract class InternalParameterValueSetter<T> {
		
		@SuppressWarnings("unchecked")
		public int setParameterValues(PreparedStatement stmt, Object[] values, int paramIdx) throws SQLException {
			int i = 0; 
			for(Object val : values) {
				i += setParameterValue(stmt, (T)val, paramIdx + i);
			}	
			return i;	
		}
		
		protected abstract int setParameterValue(PreparedStatement stmt, T value, int paramIdx) throws SQLException;
		
	}
	
	private static class BigDecimalSetter extends InternalParameterValueSetter<BigDecimal> {
		@Override
		protected int setParameterValue(PreparedStatement stmt, BigDecimal value, int paramIdx) throws SQLException {
			stmt.setBigDecimal(paramIdx, value);
			return paramIdx + 1;
		}
	}
		
	private static class BooleanSetter extends InternalParameterValueSetter<Boolean> {
		@Override
		protected int setParameterValue(PreparedStatement stmt, Boolean value, int paramIdx) throws SQLException {
			stmt.setBoolean(paramIdx, value);
			return paramIdx + 1;
		}
	}
	
	private static class DateSetter extends InternalParameterValueSetter<Date> {
		@Override
		protected int setParameterValue(PreparedStatement stmt, Date value, int paramIdx) throws SQLException {
			stmt.setObject(paramIdx, value);
			return paramIdx + 1;
		}
	}
	
	private static class IntegerSetter extends InternalParameterValueSetter<Integer> {
		@Override
		protected int setParameterValue(PreparedStatement stmt, Integer value, int paramIdx) throws SQLException {
			stmt.setInt(paramIdx, value);
			return paramIdx + 1;
		}
	}
	
	private static class StringSetter extends InternalParameterValueSetter<String> {
		@Override
		protected int setParameterValue(PreparedStatement stmt, String value, int paramIdx) throws SQLException {
			stmt.setString(paramIdx, value);
			return paramIdx + 1;
		}
	}

}
