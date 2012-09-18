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
package com.evinceframework.web.dojo.json;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Contains the stateful data needed during the serialization of a particular object graph. 
 * The stateful data needed throughout serialization is a set of object identifiers that 
 * have either already been serialized or already been queued to be serialized later, and a
 * queue of objects that need to be serialized later in the process.
 * 
 * The {@link JsonStoreEngine} and {@link JsonObjectConverter} implementations are designed to be 
 * stateless.  These objects contain no data that is specific to the particular serialization 
 * that is occurring.
 * 
 * @author Craig Swing
 */
public class JsonSerializationContext {

	private JsonStoreEngine engine;
	
	private JsonGenerator generator;
	
	private Set<String> registeredIds = new HashSet<String>();
	
	private Queue<DeferredSerialization> deferreds = new LinkedList<DeferredSerialization>();

	/* package */ JsonGenerator getGenerator() {
		return generator;
	}
	
	/* package */ JsonSerializationContext(JsonStoreEngine engine, JsonGenerator generator) {
		this.engine = engine;
		this.generator = generator;
	}
	
	/**
	 * Writes a property for the current JSON object being serialized.  This method delegates back 
	 * to the {@link JsonStoreEngine}.
	 * 
	 * @param name
	 * @param value
	 * @throws IOException
	 */
	public void writeProperty(String name, Object value) throws IOException {
		engine.writeProperty(this, name, value);
	}
	
	/* package */ DeferredSerialization popDeferred() {
		return deferreds.poll();
	}
	
	/* package */ void registerIdentifier(String identifier) {
		registeredIds.add(identifier);
	}
	
	/* package */ boolean isRegistered(String identifier) {
		return registeredIds.contains(identifier);
	}
	
	/* package */ void registerDeferredSerialization(Object value, JsonObjectConverter converter) {

		String id = converter.determineIdentifier(value);
		if (isRegistered(id)) {
			return;
		}
		
		registerIdentifier(id);
		deferreds.add(new DeferredSerialization(id, value, converter));
	}
	
	/* package */ class DeferredSerialization {
		
		private String id;
		
		private Object value;
		
		private JsonObjectConverter converter;

		public DeferredSerialization(String id, Object value, JsonObjectConverter converter) {
			this.id = id;
			this.value = value;
			this.converter = converter;
		}

		public String getIdentifier() {
			return id;
		}

		public Object getValue() {
			return value;
		}

		public JsonObjectConverter getConverter() {
			return converter;
		}
		
	}
}
