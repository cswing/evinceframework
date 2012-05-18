/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evinceframework.web.tests.dojo.json;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.springframework.util.StringUtils;

import com.evinceframework.web.dojo.json.JsonStoreEngine;

public class JsonStoreEngineTests extends TestCase {

	public void testBasicSerialization() {
		
		JsonStoreEngine engine = new JsonStoreEngine();

		Foo foo = new Foo();
		foo.name = "Test";
		
		String json = engine.serialize(foo);
		Assert.assertNotNull(json);
		Assert.assertTrue(StringUtils.hasText(json));
		Assert.assertTrue(json.indexOf("\"name\":\"Test\"")>=0);
	}
	
	public void testArraySerialization() {
		
		JsonStoreEngine engine = new JsonStoreEngine();

		Foo foo1 = new Foo();
		foo1.name = "Test 1";
		
		Foo foo2 = new Foo();
		foo2.name = "Test 2";
		
		String json = engine.serialize(new Foo[] { foo1, foo2 });
		Assert.assertNotNull(json);
		Assert.assertTrue(StringUtils.hasText(json));
		Assert.assertTrue(json.indexOf("\"name\":\"Test 1\"")>=0);
		Assert.assertTrue(json.indexOf("\"name\":\"Test 2\"")>=0);
	}
	
	public void testComplexObjectSerialization() {
		JsonStoreEngine engine = new JsonStoreEngine();

		FooBar fb = new FooBar();
		fb.name = "FooBar";
		
		fb.bar = new Bar();
		fb.bar.name = "Bar";
		
		fb.foo = new Foo();
		fb.foo.name = "Foo";
		
		String json = engine.serialize(fb);
		Assert.assertNotNull(json);
		Assert.assertTrue(StringUtils.hasText(json));
		Assert.assertTrue(json.indexOf("\"name\":\"FooBar\"")>=0);
		Assert.assertTrue(json.indexOf("\"name\":\"Bar\"")>=0);
		Assert.assertTrue(json.indexOf("\"name\":\"Foo\"")>=0);
	}

	public void testComplexObjectWithCollectionSerialization() {
		JsonStoreEngine engine = new JsonStoreEngine();

		Bar bar = new Bar();
		bar.name = "Parent";
		
		Foo foo1 = new Foo();
		foo1.name = "Test 1";
		bar.getFoos().add(foo1);
		
		Foo foo2 = new Foo();
		foo2.name = "Test 2";
		bar.getFoos().add(foo2);
		
		String json = engine.serialize(bar);
		Assert.assertNotNull(json);
		Assert.assertTrue(StringUtils.hasText(json));
		Assert.assertTrue(json.indexOf("\"name\":\"Parent\"")>=0);
		Assert.assertTrue(json.indexOf("\"name\":\"Test 1\"")>=0);
		Assert.assertTrue(json.indexOf("\"name\":\"Test 2\"")>=0);
		
	}

	public void testCyclicalSerialization() {
		JsonStoreEngine engine = new JsonStoreEngine();

		ObjectA a = new ObjectA();
		ObjectB b = new ObjectB();
		
		a.b = b;
		b.a = a;
		
		String json = engine.serialize(a);
		Assert.assertNotNull(json);
		Assert.assertTrue(StringUtils.hasText(json));
	}
	
	public void testSelfReferenceSerialization() {
		JsonStoreEngine engine = new JsonStoreEngine();

		ObjectC c = new ObjectC();
		c.c = c;
		
		String json = engine.serialize(c);
		Assert.assertNotNull(json);
		Assert.assertTrue(StringUtils.hasText(json));
	}
	
	public class Foo {
		
		private String name;

		public String getName() {
			return name;
		}
	}
	
	public class Bar {

		private String name;
		
		private List<Foo> foos = new ArrayList<Foo>();

		public String getName() {
			return name;
		}
		
		public List<Foo> getFoos() {
			return foos;
		}	
	}

	public class FooBar {
		
		private String name;

		private Foo foo;
		
		private Bar bar;
		
		public String getName() {
			return name;
		}

		public Foo getFoo() {
			return foo;
		}

		public Bar getBar() {
			return bar;
		}
	}
	
	public class ObjectA {
		
		private ObjectB b;

		public ObjectB getB() {
			return b;
		}
	}
	
	public class ObjectB {
		
		private ObjectA a;

		public ObjectA getA() {
			return a;
		}
	}
	
	public class ObjectC {
		private ObjectC c;

		public ObjectC getC() {
			return c;
		}
	}	
}
