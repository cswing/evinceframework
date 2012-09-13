package com.evinceframework.web.dojo.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

public interface JsonPrimitiveWriter<T> {

	public void writeValue(JsonGenerator generator, T value) throws IOException;
	
}
