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

import org.joda.time.LocalDate;

import com.evinceframework.web.dojo.json.JsonPrimitiveWriter;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Writes a {@link LocalDate} as a JSON object that can be interpreted on the web client in javascript.
 * 
 * <code>
 * 	{ 
 * 		"_customType": "Date",
 * 		"_value": "2013-06-01'T'00:00:00"
 * 	}
 * </code>
 * 
 * The date value is written using the ISO-8601 format.
 * 
 * @author Craig Swing
 *
 * @see JsonPrimitiveWriter
 */
public class LocalDatePrimitiveWriter implements JsonPrimitiveWriter<LocalDate> {
	
	@Override
	public void writeValue(JsonGenerator generator, LocalDate value) throws IOException {
		generator.writeStartObject();
		generator.writeStringField("_customType", "Date");
		generator.writeStringField("_value", DateTimePrimitiveWriter.dateFormat.format(value.toDate()));
		generator.writeEndObject();
	}
	
}