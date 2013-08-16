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

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;

import com.evinceframework.data.warehouse.FactTable;
import com.evinceframework.data.warehouse.query.impl.AbstractQuery;


public class PivotQuery extends AbstractQuery {

	private FactTable factTable;
	
	private SummarizationAttribute[] summarizationAttributes;
	
	private FactSelection[] factSelections;
	
	private DimensionCriterion[] dimensionCriteria = new DimensionCriterion[]{};
	
	private FactRangeCriterion[] factCriteria = new FactRangeCriterion[]{};
	
	private Integer maximumRowCount = 1000;
	
	private Locale locale = LocaleContextHolder.getLocale();
	
	public PivotQuery(FactTable factTable, FactSelection[] selections) {
		this(factTable, selections, new SummarizationAttribute[]{});
	}
	
	public PivotQuery(FactTable factTable, FactSelection[] selections, SummarizationAttribute[] summarizations) {
		this(factTable, selections, summarizations, new DimensionCriterion[]{});
	}
	
	public PivotQuery(FactTable factTable, FactSelection[] selections, SummarizationAttribute[] summarizations, 
			DimensionCriterion[] dimensionCriteria) {

		this.factTable = factTable;
		this.summarizationAttributes = summarizations != null ? summarizations : new SummarizationAttribute[]{};
		this.factSelections = selections;
		this.dimensionCriteria = dimensionCriteria;
//		this.factCriteria = factCriteria;
	}

//	@Override
	public FactTable getFactTable() {
		return factTable;
	}

//	@Override
	public SummarizationAttribute[] getSummarizations() {
		return summarizationAttributes;
	}

//	@Override
	public FactSelection[] getFactSelections() {
		return factSelections;
	}

//	@Override
	public DimensionCriterion[] getDimensionCriterion() {
		return dimensionCriteria;
	}

//	@Override
	public FactRangeCriterion[] getFactCriterion() {
		return factCriteria;
	}

//	@Override
	public Integer getMaximumRowCount() {
		return maximumRowCount;
	}

	public void setMaximumRowCount(Integer maximumRowCount) {
		this.maximumRowCount = maximumRowCount;
	}

//	@Override
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
