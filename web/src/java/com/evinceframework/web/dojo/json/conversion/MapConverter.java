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
import java.util.Map;

import com.evinceframework.web.dojo.json.JsonConverter;
import com.evinceframework.web.dojo.json.JsonSerializationContext;
import com.evinceframework.web.dojo.json.JsonStoreEngine;

/**
 * A {@link JsonConverter} for the {@link JsonStoreEngine} that will convert
 * a map of data into a JSON object.
 * 
 * The converter will inspect the map for an entry keyed by the {@link #identifierField}
 * and will use the value as the identifier.  If this key or value does not exist, it will
 * use the {@link Object#hashCode()} of the map.
 * 
 * The converter will inspect the map for an entry keyed by the {@link #typeField} and will
 * use that value as the type. If this key or value does not exist, none will be returned.
 * 
 * @author Craig Swing
 */
@SuppressWarnings("rawtypes")
public class MapConverter extends AbstractTypedJsonConverter<Map> {

	public MapConverter() {
		super(Map.class);
	}

	private String identifierField = JsonStoreEngine.DEFAULT_IDENTIFIER_NAME;
	
	private String typeField = JsonStoreEngine.DEFAULT_TYPE_NAME;

	/**
	 * The identifier property look for when {@link JsonConverter#determineIdentifier(Object)}
	 * is called.  If not specified then the {@link Object#hashCode()} will be used. 
	 * 
	 * @return
	 */
	public String getIdentifierField() {
		return identifierField;
	}

	public void setIdentifierField(String identifierField) {
		this.identifierField = identifierField;
	}

	public String getTypeField() {
		return typeField;
	}

	public void setTypeField(String typeField) {
		this.typeField = typeField;
	}

	@Override
	protected String onDetermineIdentifier(Map obj) {
		
		if (identifierField != null && obj.containsKey(identifierField)) {
			Object id = obj.get(identifierField);
			if (id != null) {
				return id.toString();
			}
		}
		
		return String.valueOf(obj.hashCode());
	}

	@Override
	protected String onDetermineType(Map obj) {
		
		if (typeField != null && obj.containsKey(typeField)) {
			Object type = obj.get(typeField);
			if (type != null) {
				return type.toString();
			}
		}
		
		return null;
	}

	@Override
	protected void onWriteObjectProperties(JsonSerializationContext context, Map map) throws IOException {
		for(Object key : map.keySet()) {
			context.writeProperty(key.toString(), map.get(key));
		}
	}

}
