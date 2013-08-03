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

import com.evinceframework.data.warehouse.FactCategory;
import com.evinceframework.data.warehouse.impl.FactCategoryImpl;
import com.evinceframework.data.warehouse.impl.FactImpl;
import com.evinceframework.data.warehouse.impl.FactTableImpl;

public class FactCategoryParsingTests extends TestCase {
	
	public void testBasicParsing() {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext("testCategories.xml", FactCategoryParsingTests.class);
		
		FactCategory category = 
				appContext.getBean("evfData.basic.factTable.testFacts.category.categoryA", FactCategoryImpl.class);
		FactTableImpl factTable = appContext.getBean("evfData.basic.factTable.testFacts", FactTableImpl.class);
		FactImpl<?> fact = appContext.getBean("evfData.basic.factTable.testFacts.fact.fact1", FactImpl.class);
		
		assertNotNull(category);
		
		assertEquals("factTable.testFacts.category.categoryA.name", category.getName());
		assertEquals("factTable.testFacts.category.categoryA.description", category.getDescription());
		
		assertNotNull(category.getFactTable());
		assertEquals(category.getFactTable(), factTable);
		
		// Facts should be in the FactCategory.fact array
		assertNotNull(category.getFacts());
		assertEquals(1, category.getFacts().length);
		assertEquals(category.getFacts()[0], fact);
		
		// Facts should still be in the FactTable.fact array
		assertNotNull(factTable.getFacts());
		assertEquals(1, factTable.getFacts().length);
		assertEquals(factTable.getFacts()[0], fact);
	}
	
	public void testCustomNameAndDescription() {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext("testCategories.xml", FactCategoryParsingTests.class);
		
		FactCategory category = 
				appContext.getBean("evfData.custom.factTable.testFacts.category.categoryA", FactCategoryImpl.class);
		
		assertNotNull(category);
		assertEquals("customName", category.getName());
		assertEquals("customDescription", category.getDescription());	
	}
	
}
