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
package com.evinceframework.tests;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.evinceframework.DomainEnumerator;
import com.evinceframework.DomainEnumeratorConverter;

public class DomainEnumeratorConverterTests extends TestCase {
	
	public void testInvalidFormat() {
		
		try {
			new DomainEnumeratorConverter<TestEnumerator>().convert("Invalid Format");
			fail();
		} catch (DomainEnumeratorConverter.InvalidConversionException e) {
			assertTrue(e.getMessage().startsWith("Invalid format"));
		}
		
	}
	
	public void testNoSuchClass() {
		
		try {
			new DomainEnumeratorConverter<TestEnumerator>().convert("java.langXXX.String::X");
			fail();
		} catch (DomainEnumeratorConverter.InvalidConversionException e) {
			assertEquals("Invalid Domain Object - java.langXXX.String", e.getMessage());
		}
		
	}

	public void testInvalidClass() {
		
		try {
			new DomainEnumeratorConverter<TestEnumerator>().convert("java.lang.String::X");
			fail();
		} catch (DomainEnumeratorConverter.InvalidConversionException e) {
			assertEquals("Invalid Domain Object - java.lang.String", e.getMessage());
		}
		
	}
	
	public void testNoFactoryMethod() {
		try {
			new DomainEnumeratorConverter<NoFactoryMethod>().convert("com.evinceframework.tests.DomainEnumeratorConverterTests$NoFactoryMethod::X");
			fail();
		} catch (DomainEnumeratorConverter.InvalidConversionException e) {
			assertEquals("No factory lookup method - com.evinceframework.tests.DomainEnumeratorConverterTests$NoFactoryMethod",
					e.getMessage());
		}
	}
	
	public void testFactoryMethod() {
		assertEquals(TestEnumerator.TEST_A, 
				new DomainEnumeratorConverter<TestEnumerator>().convert(
						"com.evinceframework.tests.DomainEnumeratorConverterTests$TestEnumerator::TEST_A"));
	}
	
	public abstract static class BaseDomainEnumerator implements DomainEnumerator {
		
		private String code;
		
		private String name;
		
		private Integer sort;
		
		public BaseDomainEnumerator(String code, String name, Integer sort) {
			this.code = code;
			this.name = name;
			this.sort = sort;
		}

		@Override
		public String getCode() {
			return code;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Integer getSort() {
			return sort;
		}
	}
	
	public static class NoFactoryMethod extends BaseDomainEnumerator {
		private NoFactoryMethod(String code, String name, Integer sort) {
			super(code, name, sort);
		}
	}
	
	public static class TestEnumerator extends BaseDomainEnumerator {
		
		public static final TestEnumerator UNKNOWN = new TestEnumerator("UNKNOWN", "UNKNOWN", 1);
		
		public static final TestEnumerator TEST_A = new TestEnumerator("TEST_A", "TEST_A", 2);
		
		public static final TestEnumerator TEST_B = new TestEnumerator("TEST_B", "TEST_B", 3);

		public static final TestEnumerator[] ALL_ENTRIES 
			= new TestEnumerator[] { UNKNOWN, TEST_A, TEST_B};

		private static final Map<String, TestEnumerator> entriesMappedByCode;
		
		public static TestEnumerator byCode(String code) {
			return entriesMappedByCode == null ? null : entriesMappedByCode.get(code);
		}
		
		static {
			Map<String, TestEnumerator> data = new HashMap<String, TestEnumerator>();
			for(TestEnumerator entry : ALL_ENTRIES) {
				if(data.containsKey(entry.getCode()))
					throw new RuntimeException(String.format("Duplicate entry code: %s", entry.getCode()));
				
				data.put(entry.getCode(), entry);
			}
			entriesMappedByCode = Collections.unmodifiableMap(data);
		}
		
		private TestEnumerator(String code, String name, Integer sort) {
			super(code, name, sort);
		}
	}
}
