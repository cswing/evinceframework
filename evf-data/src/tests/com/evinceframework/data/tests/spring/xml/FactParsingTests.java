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

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.evinceframework.data.warehouse.impl.FactImpl;
import com.evinceframework.data.warehouse.impl.FactTableImpl;

public class FactParsingTests extends TestCase {

	public void testBasicParsing() {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext("basic.xml", FactParsingTests.class);
		
		FactTableImpl factTable = appContext.getBean("evfData.basic.factTable.testFacts", FactTableImpl.class);
		FactImpl<?> fact = appContext.getBean("evfData.basic.factTable.testFacts.fact.factA", FactImpl.class);
		
		assertNotNull(fact);
		assertNotNull(fact.getFactTable());
		assertEquals(factTable, fact.getFactTable());
		assertEquals("factA", fact.getColumnName());
		
		// defaults when not specified
		assertEquals(Integer.class, fact.getValueType());
		assertEquals("factTable.testFacts.fact.factA.description", fact.getDescription());
		assertEquals("factTable.testFacts.fact.factA.name", fact.getName());
	}
	
	public void testNonDefaults() {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext("basic.xml", FactCategoryParsingTests.class);
		
		FactImpl<?> fact = appContext.getBean("evfData.custom.factTable.testFacts.fact.factA", FactImpl.class);
		
		assertNotNull(fact);
		assertEquals(BigDecimal.class, fact.getValueType());
		assertEquals("customName", fact.getName());
		assertEquals("customDescription", fact.getDescription());	
	}
	
	
	// TODO Test invalid data type
}
