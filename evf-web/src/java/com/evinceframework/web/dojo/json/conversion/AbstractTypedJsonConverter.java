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
package com.evinceframework.web.dojo.json.conversion;

import java.io.IOException;

import org.springframework.util.ClassUtils;

import com.evinceframework.web.dojo.json.JsonObjectConverter;
import com.evinceframework.web.dojo.json.JsonSerializationContext;
import com.evinceframework.web.dojo.json.JsonStoreException;

/**
 * A utility base class that allows inheriting classes to specify the type that is expected
 * to be converted.  The methods to implement have the type specified instead of {@link Object}. 
 * 
 * If an object of the incorrect type is passed in, then a {@link JsonStoreException.InvalidType} 
 * exception will be thrown.
 *  
 * @author Craig Swing
 *
 * @param <T> the expected type of the objects being converted.
 */
public abstract class AbstractTypedJsonConverter<T extends Object> implements JsonObjectConverter {

	private Class<T> clazz;
	
	/**
	 * @param clazz the expected type of the objects to be converted.
	 */
	public AbstractTypedJsonConverter(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public final String determineIdentifier(Object obj) {
		return onDetermineIdentifier(convert(obj));
	}

	/**
	 * @see JsonObjectConverter#determineIdentifier(Object)
	 */
	protected abstract String onDetermineIdentifier(T obj);

	@Override
	public final String determineType(Object obj) {
		return onDetermineType(convert(obj));
	}

	/**
	 * @see JsonObjectConverter#determineType(Object)
	 */
	protected abstract String onDetermineType(T obj);
	
	@Override
	public final void writeObjectProperties(JsonSerializationContext context, Object obj) throws IOException {
		onWriteObjectProperties(context, convert(obj));
	}

	/**
	 * @see JsonObjectConverter#writeObjectProperties(JsonSerializationContext, Object)
	 */
	protected abstract void onWriteObjectProperties(JsonSerializationContext context, T obj) throws IOException;
	
	@SuppressWarnings("unchecked")
	private T convert(Object obj) {
		if (!ClassUtils.isAssignableValue(clazz, obj)) {
			throw new JsonStoreException.InvalidType();
		}
		return (T)obj;
	}
}
