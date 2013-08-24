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
import com.evinceframework.data.warehouse.query.DrillPathEntry;
import com.evinceframework.data.warehouse.query.FactRangeCriterion;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.FactSelectionFunction;
import com.evinceframework.data.warehouse.query.HierarchicalQuery;
import com.evinceframework.data.warehouse.query.InvalidQueryException;
import com.evinceframework.data.warehouse.query.impl.FactSelectionImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HierarchicalQueryJsonResolver implements WebQueryResolver<HierarchicalQuery> {

	private BeanFactory beanFactory;
	
	private ConversionService conversionService;
	
	private MessageSourceAccessor messageSource = new MessageSourceAccessor(new WebMessageSource());
	
	public HierarchicalQueryJsonResolver(BeanFactory beanFactory, ConversionService conversionService) {
		this.beanFactory = beanFactory;
		this.conversionService = conversionService;
	}
	
	@Override
	public String getResolverKey() {
		return "evfData.query.hierarchical.json";
	}

	@Override
	public HierarchicalQuery create(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return create(webRequest.getParameter("query"));
	}
	
	@SuppressWarnings("unchecked")
	protected HierarchicalQuery create(String json) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<QueryRequest> typeRef = new TypeReference<QueryRequest>() {}; 
		QueryRequest queryRequest = mapper.readValue(json, typeRef);   
		
		// Determine Fact Table
		if(!StringUtils.hasLength(queryRequest.factTable)) {
			throw new InvalidQueryException(
					messageSource.getMessage(WebMessageSource.InvalidQueryKeys.FACT_TABLE_NOT_DEFINED));
		}
		
		FactTable factTable = beanFactory.getBean(queryRequest.factTable, FactTable.class);
		if(factTable == null) {
			throw new InvalidQueryException(
					messageSource.getMessage(
							WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT_TABLE, 
							new Object[]{ queryRequest.factTable }));
		}
		
		// Fact Selections
		List<FactSelection> factSelections = new ArrayList<FactSelection>();
		if (queryRequest.factSelections != null) {
			for(FactSelectionRequest fsr : queryRequest.factSelections) {
				
				Fact<?> fact = null;
				for(Fact<?> f : factTable.getFacts()) {
					if (f.getColumnName().equals(fsr.factKey)) {
						fact = f;
						break;
					}
				}
				
				if(fact == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(
									WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT, 
									new Object[]{ fsr.factKey, queryRequest.factTable }));
				}
				
				FactSelectionFunction function = FactSelectionFunction.byCode(fsr.function);
				if(function == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_FUNCTION, 
									new Object[]{ fsr.function, fsr.factKey, queryRequest.factTable }));
				}
				
				factSelections.add(new FactSelectionImpl(fact, function));
			}
		}
		
		// Dimension Criteria
		List<DimensionCriterion<?>> dimensionCriteria = new ArrayList<DimensionCriterion<?>>();
		if(queryRequest.dimensionCriteria != null) {
			for(DimensionCriterionRequest dcr : queryRequest.dimensionCriteria) {
				
				Dimension dimension = findDimension(dcr.dimensionKey, factTable);
				if(dimension == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(
									WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSION, 
									new Object[]{ dcr.dimensionKey, queryRequest.factTable }));
				}
				
				DimensionalAttribute<?> attribute = findDimensionalAttribute(dcr.attributeKey, dimension);
				if(attribute == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSIONAL_ATTR, 
									new Object[]{ dcr.attributeKey, dcr.dimensionKey, queryRequest.factTable }));
				}
				
				if(!StringUtils.hasLength(dcr.filterValue)) {
					throw new InvalidQueryException(
							messageSource.getMessage(WebMessageSource.InvalidQueryKeys.CRITERIA_VALUE_NOT_SPECIFIED, 
									new Object[]{ dcr.attributeKey, dcr.dimensionKey, queryRequest.factTable }));
				}
				
				if (!this.conversionService.canConvert(String.class, attribute.getValueType())) {
					throw new InvalidQueryException(
							messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_DIMENSION_CRITERIA_TYPE, 
									new Object[]{ dcr.attributeKey, dcr.dimensionKey, queryRequest.factTable }));
				}
				
				Object value = this.conversionService.convert(dcr.filterValue, attribute.getValueType());
				
				@SuppressWarnings({ "rawtypes" })
				DimensionCriterion dc = new DimensionCriterion(dimension, attribute, value);
				dimensionCriteria.add(dc);
			}
		}
		
		List<FactRangeCriterion<?>> factCriteria = new ArrayList<FactRangeCriterion<?>>();
		if(queryRequest.factCriteria != null) {
			for(FactCriterionRequest fcr : queryRequest.factCriteria) {
				
				Fact<?> fact = null;
				for(Fact<?> f : factTable.getFacts()) {
					if (f.getColumnName().equals(fcr.factKey)) {
						fact = f;
						break;
					}
				}
				
				if(fact == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(
									WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT, 
									new Object[]{ fcr.factKey, queryRequest.factTable }));
				}
				
				if (!this.conversionService.canConvert(String.class, fact.getValueType())) {
					throw new InvalidQueryException(
							messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_FACT_CRITERIA_TYPE, 
									new Object[]{ fcr.factKey, queryRequest.factTable }));
				}
				
				@SuppressWarnings("rawtypes")
				FactRangeCriterion frc = new FactRangeCriterion(fact);
				frc.setLowerBoundInclusive(fcr.isLowerBoundInclusive);
				frc.setLowerBound((Number) this.conversionService.convert(fcr.lowerBound, fact.getValueType()));
				frc.setUpperBoundInclusive(fcr.isUpperBoundInclusive);
				frc.setUpperBound((Number) this.conversionService.convert(fcr.upperBound, fact.getValueType()));
				
				factCriteria.add(frc);
			}
		}
		
		DrillPathEntry<?> root = null;
		DrillPathEntry<?> queryRoot = null;
		DrillPathEntry<?> previous = null;
		
		if(queryRequest.drillPath != null){
			for(DrillPathRequest dpr : queryRequest.drillPath) {
				
				Dimension dimension = findDimension(dpr.dimensionKey, factTable);
				if(dimension == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(
									WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSION, 
									new Object[]{ dpr.dimensionKey, queryRequest.factTable }));
				}
				
				DimensionalAttribute<?> attribute = findDimensionalAttribute(dpr.attributeKey, dimension);
				if(attribute == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSIONAL_ATTR, 
									new Object[]{ dpr.dimensionKey, dpr.attributeKey, queryRequest.factTable }));
				}
				
				@SuppressWarnings("rawtypes")
				DrillPathEntry<?> dpe = new DrillPathEntry(dimension, attribute, previous);
				
				if(StringUtils.hasText(dpr.filterValue)) {
					Object value = this.conversionService.convert(dpr.filterValue, attribute.getValueType());
					dpe.setFilterValue(value);
				}
				
				if(root == null) {
					root = dpe;
				}
				
				if(dpr.queryRoot) {
					if(queryRoot != null) {
						throw new InvalidQueryException(
								messageSource.getMessage(WebMessageSource.InvalidQueryKeys.MULTIPLE_QUERYROOTS_DEFINED));
					}
					
					queryRoot = dpe;
				}
				
				previous = dpe;
			}
			
		}
		
		return new HierarchicalQuery(factTable, 
				factSelections.toArray(new FactSelection[]{}), 
				dimensionCriteria.toArray(new DimensionCriterion[]{}), 
				factCriteria.toArray(new FactRangeCriterion[]{}),
				root, queryRoot);
	}
	
	protected Dimension findDimension(String key, FactTable factTable) {
		
		if(key != null) {
			for(Dimension d : factTable.getDimensions()) {
				if (key.equals(d.getForeignKeyColumn())) {
					return d;
				}
			}
		}
		
		return null;
	}
	
	protected DimensionalAttribute<?> findDimensionalAttribute(String key, Dimension dimension) {
		
		if(key != null) {
			for(DimensionalAttribute<?> d : dimension.getDimensionTable().getAttributes()) {
				if (key.equals(d.getColumnName())) {
					return d;
				}
			}
		}
		
		return null;
	}
	
	public static class QueryRequest {
		
		public String factTable;
		
		public FactSelectionRequest[] factSelections;
		
		public DimensionCriterionRequest[] dimensionCriteria;
		
		public FactCriterionRequest[] factCriteria;
		
		public DrillPathRequest[] drillPath;
	}
	
	public static class FactSelectionRequest {
		
		public String factKey;
		
		public String function;
	}
	
	public static class DimensionCriterionRequest {
		
		public String dimensionKey;
		
		public String attributeKey;
		
		public String filterValue;
	}
	
	public static class FactCriterionRequest {
		
		public String factKey;
		
		public String lowerBound;
		
		public boolean isLowerBoundInclusive;
		
		public String upperBound;
		
		public boolean isUpperBoundInclusive;
	}

	public static class DrillPathRequest {
		
		public String dimensionKey;
		
		public String attributeKey;
		
		public boolean queryRoot;
		
		public String filterValue;
		
	}
}
