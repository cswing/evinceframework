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
package com.evinceframework.data.warehouse;

public interface DimensionTable {

	public String getName();
	
	public String getDescription();
	
	public String getTableName();
	
	public String getPrimaryKeyColumn();
	
	/**
	 * Returns the set of {@link DimensionalAttribute}s that are considered the business key for this dimension.
	 * 
	 * @return the descriptive attribute of the table.
	 */
	@Deprecated()
	public DimensionalAttribute<? extends Object>[] getBusinessKey();
	
	public DimensionalAttribute<? extends Object>[] getAttributes();
	
	public DimensionalAttribute<? extends Object> findAttribute(String key);
	
}
