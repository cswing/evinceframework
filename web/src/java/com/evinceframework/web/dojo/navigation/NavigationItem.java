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
package com.evinceframework.web.dojo.navigation;

import java.io.IOException;

import org.springframework.util.StringUtils;

import com.evinceframework.web.dojo.json.JsonSerializationContext;
import com.evinceframework.web.dojo.json.conversion.AbstractTypedJsonConverter;

public abstract class NavigationItem {

	private String title;
	private String implementation;
	private int order;

	public NavigationItem() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public abstract static class JsonConverter<T extends NavigationItem> extends AbstractTypedJsonConverter<T> {

		public static final String TYPE = "evf.siteNav.item";

		public static final String ORDER_PROPERTY = "order";

		public static final String TITLE_PROPERTY = "title";

		public JsonConverter(Class<T> clazz) {
			super(clazz);
		}

		@Override
		protected String onDetermineIdentifier(T obj) {
			return String.valueOf(System.identityHashCode(obj));
		}

		@Override
		protected String onDetermineType(T obj) {
			return TYPE;
		}

		@Override
		protected void onWriteObjectProperties(JsonSerializationContext context, T obj) throws IOException {
			if(StringUtils.hasText(obj.getImplementation())) {
				context.writeProperty(Navigator.JsonConverter.IMPL_PROPERTY, obj.getImplementation());
			}
			context.writeProperty(TITLE_PROPERTY, obj.getTitle());
			context.writeProperty(ORDER_PROPERTY, obj.getOrder());
		}

	}

}