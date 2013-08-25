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
package com.evinceframework.data.web.mvc;

import org.springframework.context.support.ResourceBundleMessageSource;

public class WebMessageSource  extends ResourceBundleMessageSource {

	private static final String BASE_NAME = "com.evinceframework.data.i18n.evf_data_web_messages";

	public static final String UNKNOWN_ENGINE_KEY = "unknown.engine";
	
	public WebMessageSource() {
		setBasename(BASE_NAME);
	}
	
	public static class InvalidQueryKeys {
		
		public static final String INVALID_JSON = "invalidQuery.invalidJson";
		
		public static final String FACT_TABLE_NOT_DEFINED = "invalidQuery.factTable.notDefined";
		
		public static final String UNKNOWN_FACT_TABLE = "invalidQuery.factTable.unknown";
		
		public static final String UNKNOWN_FACT = "invalidQuery.fact.unknown";
		
		public static final String INVALID_FUNCTION = "invalidQuery.fact.function.invalid";
		
		public static final String UNKNOWN_DIMENSION = "invalidQuery.dimension.unknown";
		
		public static final String UNKNOWN_DIMENSIONAL_ATTR = "invalidQuery.dimensionalAttribute.unknown";
		
		public static final String CRITERIA_VALUE_NOT_SPECIFIED = "invalidQuery.missingCriteriaValue";
		
		public static final String INVALID_DIMENSION_CRITERIA_TYPE = "invalidQuery.invalidDimensionCriteriaType";
		
		public static final String INVALID_FACT_CRITERIA_TYPE = "invalidQuery.invalidFactCriteriaType";
		
		public static final String MULTIPLE_QUERYROOTS_DEFINED = "invalidQuery.multipleQueryRoots";
		
		public static final String INVALID_DRILLPATH_CRITERIA_TYPE = "invalidQuery.invalidDrillPAthCriteriaType";
	}
}
