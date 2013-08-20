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

import com.evinceframework.data.warehouse.FactTable;

public class HierarchicalQuery extends AbstractQuery {

	private static final int DEFAULT_LEVELS = 3; 
	
	private DrillPathEntry<?> root;
	
	private DrillPathEntry<?> queryRoot;
	
	private int levels = DEFAULT_LEVELS;
	
	public HierarchicalQuery(FactTable factTable, FactSelection[] factSelections,
			DimensionCriterion<?>[] dimensionCriteria, FactRangeCriterion[] factCriteria) {
		super(factTable, factSelections, dimensionCriteria, factCriteria);
	}

	public HierarchicalQuery(FactTable factTable, FactSelection[] factSelections,
			DimensionCriterion<?>[] dimensionCriteria) {
		super(factTable, factSelections, dimensionCriteria);
	}

	public HierarchicalQuery(FactTable factTable, FactSelection[] factSelections) {
		super(factTable, factSelections);
	}

	public DrillPathEntry<?> getRoot() {
		return root;
	}

	public void setRoot(DrillPathEntry<?> root) {
		this.root = root;
	}

	public DrillPathEntry<?> getQueryRoot() {
		return queryRoot;
	}

	public void setQueryRoot(DrillPathEntry<?> queryRoot) {
		this.queryRoot = queryRoot;
	}

	public int getLevels() {
		return levels;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}
	
}
