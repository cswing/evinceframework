package com.evinceframework.web.dojo.json.conversion;

import java.io.IOException;

import com.evinceframework.web.dojo.json.JsonPrimitiveWriter;
import com.fasterxml.jackson.core.JsonGenerator;

public class DefaultPrimitiveWriter<T> implements JsonPrimitiveWriter<T> {

	@Override
	public void writeValue(JsonGenerator generator, T value) throws IOException {
		generator.writeObject(value);
	}

}
