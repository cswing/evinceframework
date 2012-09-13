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

import com.evinceframework.web.dojo.json.conversion.AbstractTypedJsonConverter;

/**
 * Exceptions that can be thrown by the {@link JsonStoreEngine} during serialization. 
 * 
 * @author Craig Swing
 */
public class JsonStoreException {

	private JsonStoreException() {}
	
	/**
	 * Will be thrown during the serialization process, if the {@link JsonStoreEngine} 
	 * cannot find a {@link JsonObjectConverter} for a particular object.
	 * 
	 * This should never be thrown using the default configuration, because a default
	 * {@link JsonObjectConverter} is specified for the {@link JsonStoreEngine}.
	 *  
	 * @author Craig Swing
	 */
	public static class UnknownJsonConverter extends RuntimeException {
		private static final long serialVersionUID = 2497275634058654701L;		
	}

	/**
	 * Will be thrown during the serialization process, if the value being serialized 
	 * is considered an array, but the engine cannot determine how to iterate over the
	 * array.  
	 * 
	 * @author Craig Swing
	 */
	public static class UnknownArrayType extends RuntimeException {
		private static final long serialVersionUID = -2040847679296126608L;		
	}
	
	/**
	 * Will be thrown during the serialization process, if the value being serialized
	 * as an object is considered a javascript primitive.
	 *  
	 * @author Craig Swing
	 */
	public static class ObjectIsPrimitive extends RuntimeException {
		private static final long serialVersionUID = 668301276966218276L;		
	}
	
	/**
	 * Will be thrown during the serialization process, if the value being serialized
	 * as an object is considered an array.
	 *  
	 * @author Craig Swing
	 */
	public static class ObjectIsArray extends RuntimeException {
		private static final long serialVersionUID = 3067308124135954668L;
	}
	
	/**
	 * Will be thrown by the {@link AbstractTypedJsonConverter} if the object passed into
	 * the {@link JsonObjectConverter} methods is not of the expected type.
	 * 
	 * @author Craig Swing
	 */
	public static class InvalidType extends RuntimeException {
		private static final long serialVersionUID = -9015534416729015374L;
	}
}
