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
package com.evinceframework.core.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * A class lookup factory that uses a map of implementations keyed by the class that are used for.
 * 
 * @author Craig M. Swing
 *
 * @param <T>
 */
public class MapBackedClassLookupFactory<T extends Object> extends AbstractClassLookupFactory<T> {

	private Map<Class<?>, T> lookupMap = new HashMap<Class<?>, T>();	
	
	public Map<Class<?>, T> getLookupMap() {
		return lookupMap;
	}

	public void setLookupMap(Map<Class<?>, T> lookupMap) {
		this.lookupMap = lookupMap;
	}
	
	public void setAddToLookupMap(Map<Class<?>, T> lookupMap) {
		this.lookupMap.putAll(lookupMap);
	}

	@Override
	protected T create(Class<?> clazz) {
		return lookupMap.get(clazz);
	}
	
}