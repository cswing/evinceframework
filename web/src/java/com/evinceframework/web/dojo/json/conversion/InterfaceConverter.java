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

/**
 * An extension of the {@link PojoConverter} that will only convert the properties 
 * from the interface.
 * 
 * @author Craig Swing
 */
public class InterfaceConverter extends PojoConverter {

	private Class<?> interfaceClass;
	
	public InterfaceConverter(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	@Override
	protected boolean serializeField(PropertyDescriptor prop) {
		
		try {

			interfaceClass.getMethod(prop.getReadMethod().getName());
			
		} catch (SecurityException e) {
			return false;
		} catch (NoSuchMethodException e) {
			return false;
		}
		
		return super.serializeField(prop);
	}

}
