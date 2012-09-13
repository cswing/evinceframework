package com.evinceframework.web.dojo.json.conversion;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.evinceframework.web.dojo.json.JsonPrimitiveWriter;
import com.fasterxml.jackson.core.JsonGenerator;

public class DatePrimitiveWriter implements JsonPrimitiveWriter<Date> {

	// The default format is ISO-8601
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	@Override
	public void writeValue(JsonGenerator generator, Date value) throws IOException {
		generator.writeStartObject();
		generator.writeStringField("_customType", "Date");
		generator.writeStringField("_value", dateFormat.format(value));
		generator.writeEndObject();
	}

}
