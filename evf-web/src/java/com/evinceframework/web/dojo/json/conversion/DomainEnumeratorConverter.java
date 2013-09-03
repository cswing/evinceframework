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
package com.evinceframework.web.dojo.json.conversion;

import java.io.IOException;

import com.evinceframework.DomainEnumerator;
import com.evinceframework.web.dojo.json.JsonSerializationContext;

public class DomainEnumeratorConverter extends AbstractTypedJsonConverter<DomainEnumerator> {

	public static final String SEPERATOR = com.evinceframework.DomainEnumeratorConverter.SEPERATOR;
	
	public DomainEnumeratorConverter() {
		super(DomainEnumerator.class);
	}

	@Override
	protected String onDetermineIdentifier(DomainEnumerator obj) {
		return String.format("%s%s%s", 
				obj.getClass().getCanonicalName(), SEPERATOR, obj.getCode());
	}

	@Override
	protected String onDetermineType(DomainEnumerator obj) {
		return obj.getClass().getCanonicalName();
	}

	@Override
	protected void onWriteObjectProperties(JsonSerializationContext context, DomainEnumerator obj)
			throws IOException {
		
		context.writeProperty("code", obj.getCode());
		context.writeProperty("name", obj.getName());
		context.writeProperty("sort", obj.getSort());
	}

	
}
