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
package com.evinceframework.data.tests.spring.xml;

import junit.framework.TestCase;

import org.hibernate.dialect.Dialect;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.evinceframework.data.warehouse.impl.FactImpl;
import com.evinceframework.data.warehouse.impl.FactTableImpl;
import com.evinceframework.data.warehouse.query.impl.QueryEngineImpl;

public class WarehouseNamespaceTests extends TestCase {

	public void testBasicConfiguration() {
		
		ApplicationContext appContext = 
				new ClassPathXmlApplicationContext("basic.xml", WarehouseNamespaceTests.class);
		
		assertNotNull(appContext);
		
		Dialect dialect = appContext.getBean("evfData.basic.dialect", Dialect.class);
		assertNotNull(dialect);
		
		ResourceBundleMessageSource source = appContext.getBean("evfData.basic.messageSource", ResourceBundleMessageSource.class);
		assertNotNull(source);
		
		MessageSourceAccessor accessor = appContext.getBean("evfData.basic.messageSourceAccessor", MessageSourceAccessor.class);
		assertNotNull(accessor);
		
		QueryEngineImpl engine = appContext.getBean("evfData.basic.engine", QueryEngineImpl.class); 
		assertNotNull(engine);
		assertNotNull(engine.getDialect());
		assertEquals(dialect, engine.getDialect());
		
		// TODO DimensionTableImpl
		
		FactImpl<?> fact = appContext.getBean("evfData.basic.factTable.test.fact.factA", FactImpl.class);
		assertNotNull(fact);
		assertEquals("factA", fact.getColumnName());
		assertEquals("factTable.test.fact.factA.description", fact.getDescription());
		assertEquals("factTable.test.fact.factA.name", fact.getName());
		assertEquals(Integer.class, fact.getValueType());
		
		
		FactTableImpl factTable = appContext.getBean("evfData.basic.factTable.test", FactTableImpl.class);
		assertNotNull(factTable);
		assertNotNull(factTable.getQueryEngine());
		assertEquals(engine, factTable.getQueryEngine());
		assertEquals("factTable.test.name", factTable.getName());
		assertEquals("factTable.test.description", factTable.getDescription());
		assertNotNull(factTable.getDimensions());
//		assertEquals(1, factTable.getDimensions());
		
		assertEquals(factTable, fact.getFactTable());
		assertNotNull(factTable.getCategories());
		assertEquals(0, factTable.getCategories().length);
		assertNotNull(factTable.getFacts());
		assertEquals(1, factTable.getFacts().length);
		assertEquals(fact, factTable.getFacts()[0]);
		
	}
	
	// testCategory
	// testInValidBaseName
	// testBadDialect
}
