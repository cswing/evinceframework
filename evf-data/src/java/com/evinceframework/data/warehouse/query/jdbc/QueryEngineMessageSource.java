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
package com.evinceframework.data.warehouse.query.jdbc;

import org.springframework.context.support.ResourceBundleMessageSource;

public class QueryEngineMessageSource extends ResourceBundleMessageSource {

	private static final String BASE_NAME = "com.evinceframework.data.i18n.evf_data_messages";
	
	public static final String ROW_LIMIT_EXCEEDED = "engine.rowLimitExceeded";

	public static final String ROW_LIMIT_NOT_SUPPORTED = "engine.rowLimitUnsupported";
	
	public static final String MISSING_FACT_SELECTION = "engine.missingFactSelection";
	
	public static final String ONLY_SINGLE_FACT_SELECTION_SUPPORTED = "engine.singleFactSelectionSupported";
	
	public static final String FACT_SELECTION_REQUIRES_FUNCTION = "engine.factSelectionSupportedRequiresFunction";
	
	/**
	 * Default constructor
	 */
    public QueryEngineMessageSource() {
    	setBasename(BASE_NAME);
    }
}
