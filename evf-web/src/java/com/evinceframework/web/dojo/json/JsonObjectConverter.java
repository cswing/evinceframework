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

/**
 * The {@link JsonStoreEngine} delegates to implementations of this interface to 
 * convert specific objects based on the Java class of the object.
 * 
 * @author Craig Swing
 */
public interface JsonObjectConverter {

	/**
	 * Determine the unique identifier of the object.  Objects must have an identifier
	 * that is unique across all objects in the graph. 
	 * 
	 * @param obj the object of which the identifier is requested.
	 * @return the unique identifier of the object.
	 */
	public String determineIdentifier(Object obj);
	
	/**
	 * Determine the type of the object.  Objects can, but don't have to have a type 
	 * property.  This is used on the client to filter the data to particular seet of 
	 * objects that represent the same thing. 
	 * 
	 * If a null or an empty string is returned, the type will not be serialized to the 
	 * JSON object.
	 * 
	 * @param obj the object of which the type is requested.
	 * @return the type of the object, if the implementation supports it; otherwise null or an empty string.
	 */
	public String determineType(Object obj);
	
	/**
	 * Write the properties of the object.  Implementations do not need to write the 
	 * beginning of the JSON object, the identifier property, the type property or the
	 * end of the JSON object.  These are all handled by the {@link JsonStoreEngine}.
	 * 
	 * @param context
	 * @param obj
	 * @throws IOException
	 */
	public void writeObjectProperties(JsonSerializationContext context, Object obj) throws IOException;
	
}
