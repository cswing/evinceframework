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
package com.evinceframework.data.tests.warehouse;

import java.util.Locale;

import org.hibernate.dialect.MySQL5Dialect;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.StaticMessageSource;

import com.evinceframework.data.warehouse.Fact;
import com.evinceframework.data.warehouse.impl.DimensionImpl;
import com.evinceframework.data.warehouse.impl.DimensionTableImpl;
import com.evinceframework.data.warehouse.impl.DimensionalAttributeImpl;
import com.evinceframework.data.warehouse.impl.FactImpl;
import com.evinceframework.data.warehouse.impl.FactTableImpl;
import com.evinceframework.data.warehouse.query.QueryEngine;

public class TestData {

	private static final String DIMENSION_TABLE_A_NAME = "dimTable.a.name";
	
	private static final String DIMENSION_TABLE_A_DESC = "dimTable.a.desc";
	
	private static final String DIMENSIONAL_ATTR_A1_NAME = "dimAttr.a1.name";
	
	private static final String DIMENSIONAL_ATTR_A1_DESC = "dimAttr.a1.desc";
		
	private static final String DIMENSIONAL_ATTR_A2_NAME = "dimAttr.a2.name";
	
	private static final String DIMENSIONAL_ATTR_A2_DESC = "dimAttr.a2.desc";
		
	private static final String FACT_TABLE_FOO_DESC = "factTable.foo.desc";
	
	private static final String FACT_TABLE_FOO_NAME = "factTable.foo.name";

	private static final String FACT_SIMPLE_INTEGER_FACT_NAME_DESC = "fact.simpleIntegerFact.name.desc";
	
	private static final String FACT_SIMPLE_INTEGER_FACT_NAME = "fact.simpleIntegerFact.name";
	
	private static StaticMessageSource messageSource = new StaticMessageSource();
	
	public static final DimensionTableImpl dimensionTableA;
	
	public static final DimensionalAttributeImpl<String> dimensionalAttrA1;
	
	public static final DimensionalAttributeImpl<Integer> dimensionalAttrA2;
	
	public static final QueryEngine queryEngine;
	
	public static final FactTableImpl factTable;
	
	public static final DimensionImpl dimensionA;
	
	//public static final DimensionImpl dimensionA2;
	
	public static final Fact<Integer> simpleIntegerFact;
	
	static {
		
		Locale locale = LocaleContextHolder.getLocale();
		
		messageSource.addMessage(DIMENSION_TABLE_A_NAME, locale, "Dimension A");
		messageSource.addMessage(DIMENSION_TABLE_A_DESC, locale, "Description for 'A' dimension table.");
		messageSource.addMessage(DIMENSIONAL_ATTR_A1_NAME, locale, "Dimensional Attribute A-1");
		messageSource.addMessage(DIMENSIONAL_ATTR_A1_DESC, locale, "Description for 'A-1' dimensional attribute.");
		messageSource.addMessage(DIMENSIONAL_ATTR_A2_NAME, locale, "Dimensional Attribute A-2");
		messageSource.addMessage(DIMENSIONAL_ATTR_A2_DESC, locale, "Description for 'A-2' dimensional attribute.");
		
		messageSource.addMessage(FACT_TABLE_FOO_NAME, locale, "Foo");
		messageSource.addMessage(FACT_TABLE_FOO_DESC, locale, "Description for foo fact table.");
		messageSource.addMessage(FACT_SIMPLE_INTEGER_FACT_NAME, locale, "Simple Integer");
		messageSource.addMessage(FACT_SIMPLE_INTEGER_FACT_NAME_DESC, locale, "Description for the Simple Integer fact.");
		
		MessageSourceAccessor sourceAccessor = new MessageSourceAccessor(messageSource);
		
		// queryEngine
		queryEngine = new QueryEngine(/*new MySQL5Dialect()*/);
		
		// dimension tables
		dimensionTableA = new DimensionTableImpl(sourceAccessor, DIMENSION_TABLE_A_NAME, DIMENSION_TABLE_A_DESC, "dimA", "dimA_id");
		dimensionalAttrA1 = new DimensionalAttributeImpl<String>(sourceAccessor, DIMENSIONAL_ATTR_A1_NAME, DIMENSIONAL_ATTR_A1_DESC, dimensionTableA, "attr1", String.class, true);
		dimensionalAttrA2 = new DimensionalAttributeImpl<Integer>(sourceAccessor, DIMENSIONAL_ATTR_A2_NAME, DIMENSIONAL_ATTR_A2_DESC, dimensionTableA, "attr2", Integer.class);
		
		// build FOO fact table
		factTable = new FactTableImpl(sourceAccessor, FACT_TABLE_FOO_NAME, FACT_TABLE_FOO_DESC, queryEngine, "fooTable");
		dimensionA = new DimensionImpl(dimensionTableA, factTable, "dimA1_id");
		//dimensionA2 = new DimensionImpl(dimensionalAttrA2, factTable, "dimA2_id");
		
		simpleIntegerFact = new FactImpl<Integer>(sourceAccessor, FACT_SIMPLE_INTEGER_FACT_NAME, 
				FACT_SIMPLE_INTEGER_FACT_NAME_DESC, Integer.class, factTable, "simpleInteger");
		
	}
}
