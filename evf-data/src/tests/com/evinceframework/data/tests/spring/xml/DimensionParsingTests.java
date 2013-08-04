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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.evinceframework.data.warehouse.impl.DimensionImpl;
import com.evinceframework.data.warehouse.impl.DimensionTableImpl;
import com.evinceframework.data.warehouse.impl.FactTableImpl;

public class DimensionParsingTests extends TestCase {
	
	public void testBasicParsing() {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext("basic.xml", DimensionParsingTests.class);
		
		DimensionImpl dimension = appContext.getBean("evfData.basic.factTable.testFacts.dimension.testDimension", DimensionImpl.class);
		FactTableImpl factTable = appContext.getBean("evfData.basic.factTable.testFacts", FactTableImpl.class);
		DimensionTableImpl dimTable = appContext.getBean("evfData.basic.dimension.testDimension", DimensionTableImpl.class);
		
		assertNotNull(dimension);
		
		assertNotNull(dimension.getFactTable());
		assertEquals(factTable, dimension.getFactTable());
		
		assertNotNull(dimension.getDimensionTable());
		assertEquals(dimTable, dimension.getDimensionTable());
		
		// defaults
		assertEquals("dimension.testDimension.name", dimension.getName());
		assertEquals("dimension.testDimension.description", dimension.getDescription());
		assertEquals("testDimensionId", dimension.getForeignKeyColumn());
	}
	
	public void testNonDefaults() {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext("basic.xml", DimensionParsingTests.class);
		
		DimensionImpl dimension = appContext.getBean("evfData.custom.factTable.testFacts.dimension.testDimension", DimensionImpl.class);
		
		assertNotNull(dimension);
		
		assertEquals("customName", dimension.getName());
		assertEquals("customDescription", dimension.getDescription());
		assertEquals("customForeignKey", dimension.getForeignKeyColumn());
	}
}
