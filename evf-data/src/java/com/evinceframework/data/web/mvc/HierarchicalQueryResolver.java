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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.evinceframework.data.warehouse.Dimension;
import com.evinceframework.data.warehouse.DimensionalAttribute;
import com.evinceframework.data.warehouse.Fact;
import com.evinceframework.data.warehouse.FactTable;
import com.evinceframework.data.warehouse.query.DimensionCriterion;
import com.evinceframework.data.warehouse.query.FactRangeCriterion;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.FactSelectionFunction;
import com.evinceframework.data.warehouse.query.HierarchicalQuery;
import com.evinceframework.data.warehouse.query.InvalidQueryException;
import com.evinceframework.data.warehouse.query.impl.FactSelectionImpl;

public class HierarchicalQueryResolver implements WebQueryResolver<HierarchicalQuery> {

	private BeanFactory beanFactory;
	
	private ConversionService conversionService;
	
	private MessageSourceAccessor messageSource = new MessageSourceAccessor(new WebMessageSource());
	
	public HierarchicalQueryResolver(BeanFactory beanFactory, ConversionService conversionService) {
		this.beanFactory = beanFactory;
		this.conversionService = conversionService;
	}

	@Override
	public String getResolverKey() {
		return "evfData.query.hierarchical";
	}

	@Override
	public HierarchicalQuery create(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		
		String factTableKey = webRequest.getParameter("factTable");
		if(!StringUtils.hasLength(factTableKey)) {
			throw new InvalidQueryException(
					messageSource.getMessage(WebMessageSource.InvalidQueryKeys.FACT_TABLE_NOT_DEFINED));
		}
		
		FactTable factTable = beanFactory.getBean(factTableKey, FactTable.class);
		if(factTable == null) {
			throw new InvalidQueryException(
					messageSource.getMessage(
							WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT_TABLE, new Object[]{ factTable }));
		}
		
		List<FactSelection> factSelections = new ArrayList<FactSelection>();
		int idx = 0;
		while(processFactSelection(factSelections, webRequest, factTable, idx++)){}
		
		List<DimensionCriterion<?>> dimensionCriteria = new ArrayList<DimensionCriterion<?>>();
		idx = 0;
		while(processDimensionCriterion(dimensionCriteria, webRequest, factTable, binderFactory, idx++)){}
		
		List<FactRangeCriterion<?>> factCriteria = new ArrayList<FactRangeCriterion<?>>();
		idx = 0;
		while(processFactCriterion(factCriteria, webRequest, factTable, idx++)){}
		
		return new HierarchicalQuery(factTable, 
				factSelections.toArray(new FactSelection[]{}), 
				dimensionCriteria.toArray(new DimensionCriterion[]{}), 
				factCriteria.toArray(new FactRangeCriterion[]{}));
	}
	
	protected boolean processFactSelection(
			List<FactSelection> factSelections, NativeWebRequest webRequest, FactTable factTable, int index) 
				throws InvalidQueryException {
		
		String factKey = webRequest.getParameter(String.format("factSelection_fact_%s", index));
		if(!StringUtils.hasLength(factKey)) {
			return false;
		}
		
		Fact<?> fact = null;
		for(Fact<?> f : factTable.getFacts()) {
			if (factKey.equals(f.getColumnName())) {
				fact = f;
				break;
			}
		}
		
		if(fact == null) {
			throw new InvalidQueryException(
					messageSource.getMessage(
							WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT, new Object[]{ factKey, factTable }));
		}
		
		String functionKey = webRequest.getParameter(String.format("factSelection_function_%s", index));
		FactSelectionFunction function = FactSelectionFunction.byCode(functionKey);
		if(function == null) {
			throw new InvalidQueryException(
					messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_FUNCTION, 
							new Object[]{ functionKey, factKey, factTable }));
		}
		
		factSelections.add(new FactSelectionImpl(fact, function));
		
		return true;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean processDimensionCriterion(List<DimensionCriterion<?>> dimensionCriterion, 
			NativeWebRequest webRequest, FactTable factTable, WebDataBinderFactory binderFactory, int index) 
				throws InvalidQueryException {

		String dimKey = webRequest.getParameter(String.format("dimensionCriteria_dimension_%s", index));
		if(!StringUtils.hasLength(dimKey)) {
			return false;
		}
		
		Dimension dimension = null;
		for(Dimension d : factTable.getDimensions()) {
			if (dimKey.equals(d.getForeignKeyColumn())) {
				dimension = d;
				break;
			}
		}
		if(dimension == null) {
			throw new InvalidQueryException(
					messageSource.getMessage(
							WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSION, new Object[]{ dimKey, factTable }));
		}
		
		String dimAttr = webRequest.getParameter(String.format("dimensionCriteria_attribute_%s", index));
		if(!StringUtils.hasLength(dimAttr)) {
			return false;
		}
		
		DimensionalAttribute<?> attribute = null;
		for(DimensionalAttribute<?> d : dimension.getDimensionTable().getAttributes()) {
			if (dimAttr.equals(d.getColumnName())) {
				attribute = d;
				break;
			}
		}
		if(attribute == null) {
			throw new InvalidQueryException(
					messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSIONAL_ATTR, 
							new Object[]{ dimKey, dimAttr, factTable }));
		}
		
		String attrValue = webRequest.getParameter(String.format("dimensionCriteria_value_%s", index));
		if(!StringUtils.hasLength(attrValue)) {
			throw new InvalidQueryException(
					messageSource.getMessage(WebMessageSource.InvalidQueryKeys.CRITERIA_VALUE_NOT_SPECIFIED, 
							new Object[]{ dimKey, dimAttr, factTable }));
		}
		
		if (!this.conversionService.canConvert(String.class, attribute.getValueType())) {
			throw new InvalidQueryException(
					messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_CRITERIA_TYPE, 
							new Object[]{ dimKey, dimAttr, factTable }));
		}
		
		Object value = this.conversionService.convert(attrValue, attribute.getValueType());
		
		dimensionCriterion.add(new DimensionCriterion(dimension, attribute, value));
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	protected boolean processFactCriterion(
			List<FactRangeCriterion<?>> factCriterion, NativeWebRequest webRequest, FactTable factTable, int index) 
				throws InvalidQueryException {
		
		String factKey = webRequest.getParameter(String.format("factCriteria_fact_%s", index));
		if(!StringUtils.hasLength(factKey)) {
			return false;
		}
		
		Fact<?> fact = null;
		for(Fact<?> f : factTable.getFacts()) {
			if (factKey.equals(f.getColumnName())) {
				fact = f;
				break;
			}
		}
		
		if(fact == null) {
			throw new InvalidQueryException(
					messageSource.getMessage(
							WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT, new Object[]{ factKey, factTable }));
		}
		
		if (!this.conversionService.canConvert(String.class, fact.getValueType())) {
			throw new InvalidQueryException(
					messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_CRITERIA_TYPE, 
							new Object[]{ factKey, factTable }));
		}
		
		@SuppressWarnings({ "rawtypes" })
		FactRangeCriterion frc = new FactRangeCriterion(fact);
		
		frc.setLowerBoundInclusive(this.conversionService.convert(
				webRequest.getParameter(String.format("factCriteria_lower_inclusive_%s", index)), Boolean.class));
		frc.setLowerBound((Number) this.conversionService.convert(
				webRequest.getParameter(String.format("factCriteria_lower_value_%s", index)), fact.getValueType()));
		
		frc.setUpperBoundInclusive(this.conversionService.convert(
				webRequest.getParameter(String.format("factCriteria_upper_inclusive_%s", index)), Boolean.class));
		frc.setUpperBound((Number) this.conversionService.convert(
				webRequest.getParameter(String.format("factCriteria_upper_value_%s", index)), fact.getValueType()));
		
		factCriterion.add(frc);
		
		return true;
	}
}
