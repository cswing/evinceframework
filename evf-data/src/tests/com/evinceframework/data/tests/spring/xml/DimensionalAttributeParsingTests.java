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
import com.evinceframework.data.warehouse.impl.DimensionalAttributeImpl;

public class DimensionalAttributeParsingTests extends TestCase {

	public void testBasicParsing() {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext("basic.xml", DimensionalAttributeParsingTests.class);
		
		DimensionalAttributeImpl<?> attribute = 
				appContext.getBean("evfData.basic.dimension.testDimension.attribute.dimAttrA", DimensionalAttributeImpl.class);
		
		DimensionTableImpl dimTable = appContext.getBean("evfData.basic.dimension.testDimension", DimensionTableImpl.class);
		
		assertNotNull(attribute);
		assertNotNull(attribute.getDimensionTable());
		assertEquals(dimTable, attribute.getDimensionTable());
		assertEquals("dimAttrA", attribute.getColumnName());
		
		// Test Defaults
		assertEquals("dimension.testDimension.attribute.dimAttrA.name", attribute.getName());
		assertEquals("dimension.testDimension.attribute.dimAttrA.description", attribute.getDescription());
		assertEquals(String.class, attribute.getValueType());
	}
	
	public void testNonDefaults() {

		ApplicationContext appContext = new ClassPathXmlApplicationContext("basic.xml", DimensionalAttributeParsingTests.class);
		
		DimensionalAttributeImpl<?> attribute = 
				appContext.getBean("evfData.custom.dimension.testDimension.attribute.dimAttrA", DimensionalAttributeImpl.class);
		
		assertNotNull(attribute);

		assertEquals("customName", attribute.getName());
		assertEquals("customDescription", attribute.getDescription());
		assertEquals(Integer.class, attribute.getValueType());
	}
	
	// TODO test invalid data type
}
