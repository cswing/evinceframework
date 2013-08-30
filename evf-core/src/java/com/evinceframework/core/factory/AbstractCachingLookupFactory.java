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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractCachingLookupFactory<LOOKUP_TYPE, VALUE_TYPE> 
		implements LookupFactory<LOOKUP_TYPE, VALUE_TYPE>{

	protected Log logger = LogFactory.getLog(getClass()); 
	
	private Object lock = new Object();
		
	private VALUE_TYPE defaultImplementation = null;
	
	private boolean preventCaching = false;
	
	private Map<LOOKUP_TYPE, VALUE_TYPE> knownLookups = new HashMap<LOOKUP_TYPE, VALUE_TYPE>();
	
	private Set<LOOKUP_TYPE> unknownTypes = new HashSet<LOOKUP_TYPE>();	
	
	public AbstractCachingLookupFactory() {}
	
	/**
	 * Primary use is for development
	 * @return
	 */
	public boolean isPreventCaching() {
		return preventCaching;
	}

	public void setPreventCaching(boolean preventCaching) {
		this.preventCaching = preventCaching;
	}

	/**
	 * The caching engine will prime itself with the unknown lookup values and 
	 * findImplementation will never be called when asked for one of these values.  
	 * Also, as a result of these values already existing in the unknown set,
	 * there will never be a log entry that the caching engine is adding an unknown 
	 * lookup. 
	 * 
	 * @param ignores
	 */
	public AbstractCachingLookupFactory(LOOKUP_TYPE[] ignores) {
		unknownTypes.addAll(Arrays.asList(ignores));
	}
	
	public VALUE_TYPE getDefaultImplementation() {
		return defaultImplementation;
	}

	public void setDefaultImplementation(VALUE_TYPE defaultImplementation) {
		this.defaultImplementation = defaultImplementation;
	}

	public VALUE_TYPE lookup(LOOKUP_TYPE lookup) {		
		return lookup(lookup, true);
	}
		
	private VALUE_TYPE lookup(LOOKUP_TYPE lookup, boolean createIfNotFound) {
				
		if (preventCaching)
			return findImplementation(lookup);
		
		if(knownLookups.containsKey(lookup))
			return knownLookups.get(lookup);
		
		if(unknownTypes.contains(lookup) || !createIfNotFound)
			return null;
		
		synchronized(lock) {			
			VALUE_TYPE val = lookup(lookup, false);
			if(val != null)
				return val;
			
			val = findImplementation(lookup);
			
			if(val == null)
				val = defaultImplementation;
			
			if (val == null) {
				if(logger.isDebugEnabled())
					logger.debug(String.format("Adding unknown lookup: %s", lookup));
				
				unknownTypes.add(lookup);
			} else {
				if(logger.isDebugEnabled())
					logger.debug(String.format("Adding known lookup[%s] for key: %s", val.getClass(), lookup));
				
				knownLookups.put(lookup, val);
			}
			
			return val;
		}		
	}
	
	protected abstract VALUE_TYPE findImplementation(LOOKUP_TYPE lookup);	
	
	public void reset() {
		synchronized(lock) {
			knownLookups = new HashMap<LOOKUP_TYPE, VALUE_TYPE>();
			unknownTypes = new HashSet<LOOKUP_TYPE>();
		}
	}
	
	public int getCachedCount() {
		return knownLookups.size();
	}
	
	protected Collection<VALUE_TYPE> getCurrentCache() {
		return knownLookups.values();
	}
	
}
