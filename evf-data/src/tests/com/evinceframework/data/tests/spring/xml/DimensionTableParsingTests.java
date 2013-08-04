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

import com.evinceframework.data.warehouse.impl.DimensionTableImpl;

public class DimensionTableParsingTests extends TestCase {

	public void testBasicParsing() {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext("basic.xml", DimensionParsingTests.class);
		
		DimensionTableImpl dimTable = appContext.getBean("evfData.basic.dimension.testDimension", DimensionTableImpl.class);
		
		assertNotNull(dimTable);
		
		assertEquals("testDimension", dimTable.getTableName());
		assertNotNull(dimTable.getAttributes());
		assertEquals(1, dimTable.getAttributes().length);
		
		// Test Defaults
		assertEquals("dimension.testDimension.name", dimTable.getName());
		assertEquals("dimension.testDimension.description", dimTable.getDescription());
		assertEquals("testDimensionId", dimTable.getPrimaryKeyColumn());
	}
	
	public void testNonDefaults() {

		ApplicationContext appContext = new ClassPathXmlApplicationContext("basic.xml", DimensionParsingTests.class);
		
		DimensionTableImpl dimTable = appContext.getBean("evfData.custom.dimension.testDimension", DimensionTableImpl.class);
		
		assertNotNull(dimTable);

		assertEquals("customName", dimTable.getName());
		assertEquals("customDescription", dimTable.getDescription());
		assertEquals("customPK", dimTable.getPrimaryKeyColumn());
	}
}
