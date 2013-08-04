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

import java.util.Arrays;

import junit.framework.TestCase;

public class ObjectInstantiationTests extends TestCase {

	public void testDimensionTable_Initialization() {
		assertEquals("Dimension A", TestData.dimensionTableA.getName());
		assertEquals("Description for 'A' dimension table.", TestData.dimensionTableA.getDescription());
		assertEquals("dimA", TestData.dimensionTableA.getTableName());
		assertEquals("dimA_id", TestData.dimensionTableA.getPrimaryKeyColumn());
		assertEquals(1, TestData.dimensionTableA.getBusinessKey().length);
		assertEquals(2, TestData.dimensionTableA.getAttributes().length);
	}
	
	public void testDimensionAttribute_BusinessKeyInitialization() {
		assertEquals("Dimensional Attribute A-1", TestData.dimensionalAttrA1.getName());
		assertEquals("Description for 'A-1' dimensional attribute.", TestData.dimensionalAttrA1.getDescription());
		assertEquals(TestData.dimensionTableA, TestData.dimensionalAttrA1.getDimensionTable());
		assertEquals("attr1", TestData.dimensionalAttrA1.getColumnName());
		assertEquals(String.class, TestData.dimensionalAttrA1.getValueType());
		assertTrue(Arrays.asList(TestData.dimensionTableA.getBusinessKey()).contains(TestData.dimensionalAttrA1));
		assertTrue(Arrays.asList(TestData.dimensionTableA.getAttributes()).contains(TestData.dimensionalAttrA1));
	}
	
	public void testDimensionAttribute_NonBusinessKeyInitialization() {
		assertEquals("Dimensional Attribute A-2", TestData.dimensionalAttrA2.getName());
		assertEquals("Description for 'A-2' dimensional attribute.", TestData.dimensionalAttrA2.getDescription());
		assertEquals(TestData.dimensionTableA, TestData.dimensionalAttrA2.getDimensionTable());
		assertEquals("attr2", TestData.dimensionalAttrA2.getColumnName());
		assertEquals(Integer.class, TestData.dimensionalAttrA2.getValueType());
		assertFalse(Arrays.asList(TestData.dimensionTableA.getBusinessKey()).contains(TestData.dimensionalAttrA2));
		assertTrue(Arrays.asList(TestData.dimensionTableA.getAttributes()).contains(TestData.dimensionalAttrA2));
	}
	
	public void testFactTable_Initialization() {
		assertEquals("Foo", TestData.factTable.getName());
		assertEquals("Description for foo fact table.", TestData.factTable.getDescription());
		assertEquals("fooTable", TestData.factTable.getTableName());
		assertEquals(1, TestData.factTable.getDimensions().length);
		assertEquals(1, TestData.factTable.getFacts().length);
	}

	public void testFact_Initialization() {
		assertEquals("Simple Integer", TestData.simpleIntegerFact.getName());
		assertEquals("Description for the Simple Integer fact.", TestData.simpleIntegerFact.getDescription());
		assertEquals("simpleInteger", TestData.simpleIntegerFact.getColumnName());
		assertEquals(Integer.class, TestData.simpleIntegerFact.getValueType());
		assertEquals(TestData.factTable, TestData.simpleIntegerFact.getFactTable());
		assertTrue(Arrays.asList(TestData.factTable.getFacts()).contains(TestData.simpleIntegerFact));
	}

	public void testDimension_Initilaization() {
		assertEquals(TestData.factTable, TestData.dimensionA.getFactTable());
		assertEquals(TestData.dimensionTableA, TestData.dimensionA.getDimensionTable());
		assertEquals("dimA1_id", TestData.dimensionA.getForeignKeyColumn());
	}
	
}
