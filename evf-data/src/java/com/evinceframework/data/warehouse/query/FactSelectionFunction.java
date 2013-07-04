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
package com.evinceframework.data.warehouse.query;

public class FactSelectionFunction {
	
	public static final FactSelectionFunction AVG = new FactSelectionFunction("AVG");
	
	public static final FactSelectionFunction COUNT = new FactSelectionFunction("COUNT");
	
	public static final FactSelectionFunction MAX = new FactSelectionFunction("MAX");
	
	public static final FactSelectionFunction MIN = new FactSelectionFunction("MIN");
	
	public static final FactSelectionFunction STD = new FactSelectionFunction("STD");
	
	public static final FactSelectionFunction SUM = new FactSelectionFunction("SUM");
	
	private String syntax;
	
	protected FactSelectionFunction(String syntax) {
		this.syntax = syntax;
	}

	public String getSyntax() {
		return syntax;
	}	
}
