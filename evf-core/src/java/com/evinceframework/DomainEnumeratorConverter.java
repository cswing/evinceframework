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
package com.evinceframework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.evinceframework.core.factory.AbstractCachingLookupFactory;

public class DomainEnumeratorConverter<T extends DomainEnumerator> implements Converter<String, T> {

	public static final String SEPERATOR = "::";
	
	public static final String FACTORY_METHOD = "byCode";
	
	private static final MethodLookupFactory METHOD_LOOKUP_FACTORY = new MethodLookupFactory();
	
	@Override
	@SuppressWarnings("unchecked")
	public T convert(String source) {
		
		String[] values = StringUtils.split(source, SEPERATOR);
		
		if(values == null) {
			throw new InvalidConversionException("Invalid format");
		}
		
		String className = values[0];
		Class<?> clazz = null;
		
		try {
			clazz = ClassUtils.forName(className, getClass().getClassLoader());
		} catch (Exception e) {
			throw new InvalidConversionException("Invalid Domain Object - " + className, e);
		}
		
		if (!ClassUtils.isAssignable(DomainEnumerator.class, clazz)) {
			throw new InvalidConversionException("Invalid Domain Object - " + className);
		}
		
		Method method = METHOD_LOOKUP_FACTORY.lookup(clazz);
		if (method == null) {
			throw new InvalidConversionException("No factory lookup method - " + className);
		}
		
		try {
			
			return (T) method.invoke(null, values[1]);
			
		} catch (IllegalArgumentException e) {
			throw new InvalidConversionException("Unexpected factory lookup exception", e);
		} catch (IllegalAccessException e) {
			throw new InvalidConversionException("Unexpected factory lookup exception", e);
		} catch (InvocationTargetException e) {
			throw new InvalidConversionException("Unexpected factory lookup exception", e);
		}
	}

	private static class MethodLookupFactory extends AbstractCachingLookupFactory<Class<?>, Method> {

		@Override
		protected Method findImplementation(Class<?> lookup) {
			try {
				return lookup.getMethod(FACTORY_METHOD, new Class<?>[] { String.class });
				
			} catch (SecurityException e) {
				return null;
			} catch (NoSuchMethodException e) {
				return null;
			}
		}
	}
	
	public static class InvalidConversionException extends RuntimeException {

		private static final long serialVersionUID = 502981243515276314L;

		public InvalidConversionException(String message, Throwable cause) {
			super(message, cause);
		}

		public InvalidConversionException(String message) {
			super(message);
		}
	}
}
