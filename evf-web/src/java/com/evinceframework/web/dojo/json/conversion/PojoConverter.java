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

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import com.evinceframework.web.dojo.json.JsonObjectConverter;
import com.evinceframework.web.dojo.json.JsonSerializationContext;
import com.evinceframework.web.dojo.json.JsonStoreEngine;

/**
 * The default {@link JsonObjectConverter} for the {@link JsonStoreEngine}.  This converter
 * by default will convert all properties on the object.
 * 
 * The converter will inspect the object for an entry keyed by the {@link #identifierField}
 * and will use the value as the identifier.  If this key or value does not exist, it will
 * use the {@link Object#hashCode()} of the map.
 *
 * This converter can operate in two modes, determined by the {@link #includeFieldsByDefault} 
 * field.
 * 
 * <ul>
 * <li>Include all fields, with the ability to specify exclusions.  This is the default and 
 * will serialize all properties unless they are in the {@link PojoConverter#overriddenFields}.</li>
 * 
 * <li>Exclude all fields, with the ability to specify inclusions. This will only serialize the 
 * fields specified by {@link PojoConverter#overriddenFields}.
 * </ul>
 * 
 * @author Craig Swing
 */
public class PojoConverter implements JsonObjectConverter {
	
	private String identifierField = JsonStoreEngine.DEFAULT_IDENTIFIER_NAME;
	
	private String type;
	
	private boolean includeFieldsByDefault = true;
	
	private Set<String> overriddenFields = Collections.unmodifiableSet(
			new HashSet<String>(Arrays.asList(new String[] { "class" } )));

	/**
	 * The identifier property look for when {@link JsonObjectConverter#determineIdentifier(Object)}
	 * is called.  If not specified then the {@link Object#hashCode()} will be used. 
	 * 
	 * @return
	 */
	public String getIdentifierField() {
		return identifierField;
	}

	/**
	 * Override the identifier field used by the  
	 * 
	 * @param identifierField
	 */
	public void setIdentifierField(String identifierField) {
		this.identifierField = identifierField;
	}

	/**
	 * The type to return {@link JsonObjectConverter#determineType(Object)}.  If the type
	 * is not specified then the canonical name of the java class will be used.  
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Provide the ability to specify the type to use with {@link JsonObjectConverter#determineType(Object)}
	 * when the instance of this converter is specific to a class.
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * The mode that this instance of the converter is operating in. The default is true;
	 * 
	 * @return the serialization mode.
	 */
	public boolean includeFieldsByDefault() {
		return includeFieldsByDefault;
	}

	/**
	 * Change the mode that this instance of the converter should operate in.
	 * 
	 * @param includeFieldsByDefault the serialization mode. true includes fields by default;
	 * false excludes fields by default.
	 */
	public void setIncludeFieldsByDefault(boolean includeFieldsByDefault) {
		this.includeFieldsByDefault = includeFieldsByDefault;
	}

	/**
	 * If {@link #includeFieldsByDefault} is true, then these are the fields that will NOT 
	 * be serialized. If {@link #includeFieldsByDefault} is false, then these are the fields 
	 * that will be serialized. 
	 * 
	 * @return the fields to include or exclude
	 */
	public Set<String> getOverriddenFields() {
		return overriddenFields;
	}

	/**
	 * Set the fields that should or should not be serialized, depending on the value of
	 * {@link #includeFieldsByDefault}.
	 * 
	 * @param overriddenFields the fields to include or exclude
	 */
	public void setOverriddenFields(Set<String> overriddenFields) {
		this.overriddenFields = Collections.unmodifiableSet(overriddenFields);
	}

	/**
	 * Set the fields that should or should not be serialized, depending on the value of
	 * {@link #includeFieldsByDefault}.
	 * 
	 * @param overriddenFields the fields to include or exclude
	 */
	public void setOverriddenFields(String[] overriddenFields) {
		Set<String> fields = new HashSet<String>();
		for (String fld : overriddenFields) {
			fields.add(fld);
		}
		setOverriddenFields(fields);
	}
	
	@Override
	public String determineType(Object obj) {
		
		if (type != null) {
			return type;
		}
		
		if (obj != null) {
			return obj.getClass().getCanonicalName();
		}
		
		return null; 
	}

	@Override
	public void writeObjectProperties(JsonSerializationContext context, Object obj) throws IOException {
		
		BeanWrapperImpl bean = new BeanWrapperImpl(obj);		
		
		for(PropertyDescriptor prop : bean.getPropertyDescriptors()) {			
			if(serializeField(prop)) {				
				try {
					
					context.writeProperty(prop.getName(), bean.getPropertyValue(prop.getName()));
					
				} catch (BeansException e) {
					// TODO log error
					//e.printStackTrace();
				}				
			}			
		}
	}

	@Override
	public String determineIdentifier(Object obj) {
		return String.valueOf(System.identityHashCode(obj));
	}

	protected boolean serializeField(PropertyDescriptor prop) {
		
		String fieldName = prop.getName();
		
		// if fields are included by default, only return false if the overridden fields CONTAINS the field.
		if(includeFieldsByDefault)
			return !overriddenFields.contains(fieldName);
		
		// else !includeFieldsByDefault
		// fields must be specifically identified for serialization 
		return overriddenFields.contains(fieldName);			
	}	
}
