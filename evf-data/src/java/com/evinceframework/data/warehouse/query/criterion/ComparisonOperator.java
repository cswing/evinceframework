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
package com.evinceframework.data.warehouse.query.criterion;

public class ComparisonOperator {

	public static final ComparisonOperator EQUALS = new ComparisonOperator();
	
	public static final ComparisonOperator NOT_EQUALS = new ComparisonOperator();
	
	public static final ComparisonOperator GREATER_THAN = new ComparisonOperator();
	
	public static final ComparisonOperator GREATER_THAN_OR_EQUALS = new ComparisonOperator();
	
	public static final ComparisonOperator LESS_THAN = new ComparisonOperator();
	
	public static final ComparisonOperator LESS_THAN_OR_EQUALS = new ComparisonOperator();
	
	private ComparisonOperator() {}
	
}
