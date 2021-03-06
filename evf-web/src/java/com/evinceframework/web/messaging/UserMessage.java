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
package com.evinceframework.web.messaging;

import java.io.IOException;

import com.evinceframework.web.dojo.json.JsonSerializationContext;
import com.evinceframework.web.dojo.json.conversion.AbstractTypedJsonConverter;

/**
 * A UserMessage is a message that is to be displayed to the user.
 * 
 * @author Craig Swing
 */
public class UserMessage {

	private UserMessageType type;
	
	private String code;
	
	private String message;
	
	private String field;
	
	private String description;	
	
	public UserMessage(UserMessageType type, String code, String message, String description) {
		this(type, code, message, description, null);
	}
	
	public UserMessage(UserMessageType type, String code, String message, String description, String field) {
		this.type = type;
		this.code = code;
		this.message = message;
		this.description = description;
		this.field = field;
	}

	/**
	 * The type of message.
	 * 
	 * @return the message type
	 */
	public UserMessageType getMessageType() {
		return type;
	}

	/**
	 * A code that can be used to identify the specific message.
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * The message to display to the user.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * A more detailed description to the message.  In the case of an error message, the 
	 * description could provide more details as to why the error might occur.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * The field, if appropriate, that this message is for.
	 *  
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	
	public static class JsonConverter extends AbstractTypedJsonConverter<UserMessage>{

		public static final String TYPE = "evf.msg";
		
		private static final String MSG_TYPE_PROPERTY = "msgType";
		private static final String CODE_PROPERTY = "code";
		private static final String MESSAGE_PROPERTY = "message";
		private static final String DESCRIPTION_PROPERTY = "description";
		private static final String FIELD_PROPERTY = "field";
		
		public JsonConverter() {
			super(UserMessage.class);
		}

		@Override
		protected String onDetermineIdentifier(UserMessage userMessage) {
			return String.valueOf(System.identityHashCode(userMessage));
		}

		@Override
		protected String onDetermineType(UserMessage userMessage) {
			return TYPE;
		}

		@Override
		protected void onWriteObjectProperties(JsonSerializationContext context, UserMessage userMessage)
				throws IOException {			
			context.writeProperty(MSG_TYPE_PROPERTY, userMessage.getMessageType().typeCode);
			context.writeProperty(CODE_PROPERTY, userMessage.getCode());
			context.writeProperty(MESSAGE_PROPERTY, userMessage.getMessage());
			context.writeProperty(DESCRIPTION_PROPERTY, userMessage.getDescription());
			context.writeProperty(FIELD_PROPERTY, userMessage.getField());
		}
	}
}
