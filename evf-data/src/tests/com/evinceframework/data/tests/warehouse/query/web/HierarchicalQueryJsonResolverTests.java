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
package com.evinceframework.data.tests.warehouse.query.web;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import com.evinceframework.data.warehouse.query.DrillPathEntry;
import com.evinceframework.data.warehouse.query.FactSelectionFunction;
import com.evinceframework.data.warehouse.query.HierarchicalQuery;
import com.evinceframework.data.warehouse.query.InvalidQueryException;
import com.evinceframework.data.web.mvc.HierarchicalQueryJsonResolver;
import com.evinceframework.data.web.mvc.WebMessageSource;

public class HierarchicalQueryJsonResolverTests extends TestCase {

	private static final DefaultConversionService conversionService = new DefaultConversionService();
	
	private static final MessageSourceAccessor messageSource = new MessageSourceAccessor(new WebMessageSource());
	
	public void testParse() throws Exception {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		HierarchicalQuery query = resolver.testParse(
				"{ \"factTable\": \"evfData.basic.factTable.testFacts\", "
				+ "\"factSelections\": [{ \"factKey\": \"factA\" }, { \"factKey\": \"factB\", \"function\":\"SUM\" }],"
				+ "\"dimensionCriteria\": [{ \"dimensionKey\": \"testDimensionId\", \"attributeKey\":\"dimAttrA\", \"filterValue\":\"1\" }, "
					+ "{ \"dimensionKey\": \"testDimension2Id\", \"attributeKey\":\"dimAttr2B\", \"filterValue\":\"2\"}], "
				+ "\"factCriteria\": [{ \"factKey\": \"factA\", \"lowerBound\": \"1\", \"isLowerBoundInclusive\": true, \"upperBound\": \"5\", \"isUpperBoundInclusive\": true },"
					+ "{ \"factKey\": \"factB\", \"lowerBound\": \"2\", \"isLowerBoundInclusive\": false, \"upperBound\": \"6\" }],"
				+ "\"drillPath\": [{ \"dimensionKey\": \"testDimensionId\", \"attributeKey\":\"dimAttrA\", \"queryRoot\": true, \"filterValue\": \"5\"},"
					+ "{ \"dimensionKey\": \"testDimension2Id\", \"attributeKey\":\"dimAttr2B\", \"filterValue\": \"7\"}] }");
		
		assertNotNull(query.getFactTable());
		assertEquals("testFacts", query.getFactTable().getTableName());
		
		assertNotNull(query.getFactSelections());
		assertEquals(2, query.getFactSelections().length);
		
		assertNotNull(query.getFactSelections()[0]);
		assertEquals("factA", query.getFactSelections()[0].getFact().getColumnName());
		assertNull(query.getFactSelections()[0].getFunction());
		
		assertNotNull(query.getFactSelections()[1]);
		assertEquals("factB", query.getFactSelections()[1].getFact().getColumnName());
		assertEquals(FactSelectionFunction.SUM, query.getFactSelections()[1].getFunction());

		assertNotNull(query.getDimensionCriterion());
		assertEquals(2, query.getDimensionCriterion().length);
		
		assertNotNull(query.getDimensionCriterion()[0]);
		assertEquals("testDimensionId", query.getDimensionCriterion()[0].getDimension().getForeignKeyColumn());
		assertEquals("dimAttrA", query.getDimensionCriterion()[0].getDimensionalAttribute().getColumnName());
		assertNotNull(query.getDimensionCriterion()[0].getValues());
		assertEquals(1, query.getDimensionCriterion()[0].getValues().length);
		assertEquals(1, query.getDimensionCriterion()[0].getValues()[0]);
		
		assertNotNull(query.getDimensionCriterion()[1]);
		assertEquals("testDimension2Id", query.getDimensionCriterion()[1].getDimension().getForeignKeyColumn());
		assertEquals("dimAttr2B", query.getDimensionCriterion()[1].getDimensionalAttribute().getColumnName());
		assertNotNull(query.getDimensionCriterion()[1].getValues());
		assertEquals(1, query.getDimensionCriterion()[1].getValues().length);
		assertEquals(2, query.getDimensionCriterion()[1].getValues()[0]);
		
		assertNotNull(query.getFactCriterion());
		assertEquals(2, query.getFactCriterion().length);
		
		assertNotNull(query.getFactCriterion()[0]);
		assertEquals("factA", query.getFactCriterion()[0].getFact().getColumnName());
		assertEquals(1, query.getFactCriterion()[0].getLowerBound());
		assertEquals(true, query.getFactCriterion()[0].isLowerBoundInclusive());
		assertEquals(5, query.getFactCriterion()[0].getUpperBound());
		assertEquals(true, query.getFactCriterion()[0].isUpperBoundInclusive());
		
		assertNotNull(query.getFactCriterion()[1]);
		assertEquals("factB", query.getFactCriterion()[1].getFact().getColumnName());
		assertEquals(2, query.getFactCriterion()[1].getLowerBound());
		assertEquals(false, query.getFactCriterion()[1].isLowerBoundInclusive());
		assertEquals(6, query.getFactCriterion()[1].getUpperBound());
		assertEquals(false, query.getFactCriterion()[1].isUpperBoundInclusive());
		
		assertNotNull(query.getRoot());
		assertNotNull(query.getQueryRoot());
		
		List<DrillPathEntry<?>> entries = new ArrayList<DrillPathEntry<?>>();
		DrillPathEntry<?> entry = query.getRoot();
		while(entry != null) {
			entries.add(entry);
			entry = entry.getNextEntry();
		}
		
		assertEquals(2, entries.size());
		
		assertEquals("testDimensionId", entries.get(0).getDimension().getForeignKeyColumn());
		assertEquals("dimAttrA", entries.get(0).getDimensionalAttribute().getColumnName());
		assertEquals(5, entries.get(0).getFilterValue());
		assertEquals(entries.get(0), query.getRoot());
		assertEquals(query.getRoot(), query.getQueryRoot());
		
		assertEquals("testDimension2Id", entries.get(1).getDimension().getForeignKeyColumn());
		assertEquals("dimAttr2B", entries.get(1).getDimensionalAttribute().getColumnName());
		assertEquals(7, entries.get(1).getFilterValue());
	}
	
	public void testNullData() throws Exception {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		assertNull(resolver.testParse(null));
	}
	
	public void testEmptyData() throws Exception {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		assertNull(resolver.testParse("   "));
	}
	
	public void testInvalidJson() throws Exception {
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		try {
			resolver.testParse("XYZ");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_JSON), e.getMessage());
		}
			
	}
	
	public void testMissingFactTable() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.FACT_TABLE_NOT_DEFINED), e.getMessage());
		}
	}
	
	public void testUnknownFactTable() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"XYZ\" }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT_TABLE), e.getMessage());
		}
	}
	
	public void testFactSelection_UnknownFact() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"factSelections\": [{ \"factKey\": \"XYZ\" }] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT), e.getMessage());
		}
	}
	
	public void testFactSelection_InvalidFunction() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"factSelections\": [{ \"factKey\": \"factA\", \"function\": \"XYZ\" }] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_FUNCTION), e.getMessage());
		}
	}
	
	public void testDimensionCriteria_UnknownDimension() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"dimensionCriteria\": [{ \"dimensionKey\": \"XYZ\" }] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSION), e.getMessage());
		}
	}
	
	public void testDimensionCriteria_UnknownDimensionalAttr() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"dimensionCriteria\": [{ \"dimensionKey\": \"testDimensionId\", \"attributeKey\":\"XYZ\"}] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSIONAL_ATTR), e.getMessage());
		}
	}
	
	public void testDimensionCriteria_ValueNotSpecified() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"dimensionCriteria\": [{ \"dimensionKey\": \"testDimensionId\", \"attributeKey\":\"dimAttrA\"}] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.CRITERIA_VALUE_NOT_SPECIFIED), e.getMessage());
		}
	}
	
	public void testDimensionCriteria_InvalidCriteriaType() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"dimensionCriteria\": [{ \"dimensionKey\": \"testDimensionId\", \"attributeKey\":\"dimAttrA\", \"filterValue\":\"XYZ\"}] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_DIMENSION_CRITERIA_TYPE), e.getMessage());
		}
	}
	
	public void testFactRangeCriteria_UnknownFact() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"factCriteria\": [{ \"factKey\": \"XYZ\" }] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT), e.getMessage());
		}
	}

	public void testFactRangeCriteria_InvalidCriteriaType_LowerBound() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"factCriteria\": [{ \"factKey\": \"factA\", \"lowerBound\": \"XYZ\" }] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_FACT_CRITERIA_TYPE), e.getMessage());
		}
	}
	
	public void testFactRangeCriteria_InvalidCriteriaType_UpperBound() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"factCriteria\": [{ \"factKey\": \"factA\", \"lowerBound\": \"1\", \"upperBound\": \"XYZ\" }] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_FACT_CRITERIA_TYPE), e.getMessage());
		}
	}
	
	public void testDrillPath_UnknownDimension() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"drillPath\": [{ \"dimensionKey\": \"XYZ\" }] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSION), e.getMessage());
		}
	}
	
	public void testDrillPath_UnknownDimensionalAttribute() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"drillPath\": [{ \"dimensionKey\": \"testDimensionId\", \"attributeKey\":\"XYZ\" }] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSIONAL_ATTR), e.getMessage());
		}
	}
	
	public void testDrillPath_MultipleQueryRoot() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"drillPath\": [{ \"dimensionKey\": \"testDimensionId\", \"attributeKey\":\"dimAttrA\", \"queryRoot\": true }, { \"dimensionKey\": \"testDimensionId\", \"attributeKey\":\"dimAttrB\", \"queryRoot\": true }] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.MULTIPLE_QUERYROOTS_DEFINED), e.getMessage());
		}
	}
	
	public void testDrillPath_InvalidCriteriaType() throws Exception {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resolver.xml", HierarchicalQueryJsonResolverTests.class);
		
		TestResolver resolver = new TestResolver(appContext);
		
		try {
			resolver.testParse("{ \"factTable\": \"evfData.basic.factTable.testFacts\", \"drillPath\": [{ \"dimensionKey\": \"testDimensionId\", \"attributeKey\":\"dimAttrA\", \"queryRoot\": true, \"filterValue\": \"XYZ\"}] }");
			fail();
		} catch (InvalidQueryException e) {
			assertEquals(messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_DRILLPATH_CRITERIA_TYPE), e.getMessage());
		}
	}
	
	public class TestResolver extends HierarchicalQueryJsonResolver {
		
		public TestResolver(ApplicationContext appContext) {
			super(appContext, conversionService);
		}
		
		public TestResolver(ApplicationContext appContext, ConversionService conversionService) {
			super(appContext, conversionService);
		}

		public HierarchicalQuery testParse(String json) throws Exception{
			return create(json);
		}
	}
}
