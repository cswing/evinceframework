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
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.evinceframework.data.warehouse.Dimension;
import com.evinceframework.data.warehouse.DimensionalAttribute;
import com.evinceframework.data.warehouse.Fact;
import com.evinceframework.data.warehouse.FactTable;
import com.evinceframework.data.warehouse.query.DrillPathEntry;
import com.evinceframework.data.warehouse.query.FactSelection;
import com.evinceframework.data.warehouse.query.FactSelectionFunction;
import com.evinceframework.data.warehouse.query.HierarchicalQuery;
import com.evinceframework.data.warehouse.query.InvalidQueryException;
import com.evinceframework.data.warehouse.query.criterion.Criterion;
import com.evinceframework.data.warehouse.query.criterion.Expressions;
import com.evinceframework.data.warehouse.query.impl.FactSelectionImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Responsible for turning a json object that is part of the HTTP request into a {@link HierarchicalQuery}.
 * 
 * @author Craig Swing
 *
 */
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
		
		// TODO use attribute to determine parameter to use 
		
		return create(webRequest.getParameter("query"));
	}
	
	@SuppressWarnings("unchecked")
	protected HierarchicalQuery create(String json) throws Exception {
		
		if(!StringUtils.hasText(json))
			return null;
		
		QueryRequest queryRequest = null;
				
		try {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<QueryRequest> typeRef = new TypeReference<QueryRequest>() {}; 
			queryRequest = mapper.readValue(json, typeRef);
		} catch (JsonProcessingException jpe) {
			throw new InvalidQueryException(
					messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_JSON), jpe);
		}   
		
		// Determine Fact Table
		if(!StringUtils.hasLength(queryRequest.factTable)) {
			throw new InvalidQueryException(
					messageSource.getMessage(WebMessageSource.InvalidQueryKeys.FACT_TABLE_NOT_DEFINED));
		}
				
		if(!assertBeanExistance(queryRequest.factTable, FactTable.class)) {
			throw new InvalidQueryException(
					messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT_TABLE, 
							new Object[]{ queryRequest.factTable }));
		}
		
		FactTable factTable = beanFactory.getBean(queryRequest.factTable, FactTable.class);
		
		// Fact Selections
		List<FactSelection> factSelections = new ArrayList<FactSelection>();
		if (queryRequest.factSelections != null) {
			for(FactSelectionRequest fsr : queryRequest.factSelections) {
				
				Fact<?> fact = factTable.findFact(fsr.factKey);
				if(fact == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(
									WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT, 
									new Object[]{ fsr.factKey, queryRequest.factTable }));
				}
				
				FactSelectionFunction function = null;
				if(fsr.function != null) {
					function = FactSelectionFunction.byCode(fsr.function);
					if(function == null) {
						throw new InvalidQueryException(
								messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_FUNCTION, 
										new Object[]{ fsr.function, fsr.factKey, queryRequest.factTable }));
					}
				}
				
				factSelections.add(new FactSelectionImpl(fact, function));
			}
		}
		
		// Dimension Criteria
		List<Criterion> criteria = new ArrayList<Criterion>();
		if(queryRequest.dimensionCriteria != null) {
			for(DimensionCriterionRequest dcr : queryRequest.dimensionCriteria) {
				
				Dimension dimension = factTable.findDimension(dcr.dimensionKey);
				if(dimension == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(
									WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSION, 
									new Object[]{ dcr.dimensionKey, queryRequest.factTable }));
				}
				
				DimensionalAttribute<?> attribute = dimension.getDimensionTable().findAttribute(dcr.attributeKey);
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
				
				Object value = null;
				
				try {
					value = this.conversionService.convert(dcr.filterValue, attribute.getValueType());
				} catch (ConversionException ce) {
					throw new InvalidQueryException(
							messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_DIMENSION_CRITERIA_TYPE, 
									new Object[]{ dcr.attributeKey, dcr.dimensionKey, queryRequest.factTable }), ce);
				}
				
				criteria.add(Expressions.eq(dimension, attribute, value));
			}
		}
		
		// Fact Criterion
		if(queryRequest.factCriteria != null) {
			for(FactCriterionRequest fcr : queryRequest.factCriteria) {
				
				Fact<?> fact = factTable.findFact(fcr.factKey);
				if(fact == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(
									WebMessageSource.InvalidQueryKeys.UNKNOWN_FACT, 
									new Object[]{ fcr.factKey, queryRequest.factTable }));
				}
				
				try {
				
					if(fcr.isLowerBoundInclusive) {
						criteria.add(Expressions.greaterThenOrEqual(fact, 
								this.conversionService.convert(fcr.lowerBound, fact.getValueType())));
						
					} else {
						criteria.add(Expressions.greaterThen(fact, 
								this.conversionService.convert(fcr.lowerBound, fact.getValueType())));
					}
					
					if(fcr.isUpperBoundInclusive) {
						criteria.add(Expressions.lessThenOrEqual(fact, 
								this.conversionService.convert(fcr.upperBound, fact.getValueType())));
						
					} else {
						criteria.add(Expressions.lessThen(fact, 
								this.conversionService.convert(fcr.upperBound, fact.getValueType())));
					}
					
				} catch (ConversionException ce) {
					throw new InvalidQueryException(
							messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_FACT_CRITERIA_TYPE, 
									new Object[]{ fcr.factKey, queryRequest.factTable }), ce);
				}
			}
		}
		
		DrillPathEntry<?> root = null;
		DrillPathEntry<?> queryRoot = null;
		DrillPathEntry<?> previous = null;
		
		if(queryRequest.drillPath != null){
			for(DrillPathRequest dpr : queryRequest.drillPath) {
				
				Dimension dimension = factTable.findDimension(dpr.dimensionKey);
				if(dimension == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSION, 
									new Object[]{ dpr.dimensionKey, queryRequest.factTable }));
				}
				
				DimensionalAttribute<?> attribute = dimension.getDimensionTable().findAttribute(dpr.attributeKey);
				if(attribute == null) {
					throw new InvalidQueryException(
							messageSource.getMessage(WebMessageSource.InvalidQueryKeys.UNKNOWN_DIMENSIONAL_ATTR, 
									new Object[]{ dpr.dimensionKey, dpr.attributeKey, queryRequest.factTable }));
				}
				
				@SuppressWarnings("rawtypes")
				DrillPathEntry<?> dpe = new DrillPathEntry(dimension, attribute, previous);
				
				if(StringUtils.hasText(dpr.filterValue)) {
					try {
						Object value = this.conversionService.convert(dpr.filterValue, attribute.getValueType());
						dpe.setFilterValue(value);
					} catch (ConversionException ce) {
						throw new InvalidQueryException(
								messageSource.getMessage(WebMessageSource.InvalidQueryKeys.INVALID_DRILLPATH_CRITERIA_TYPE, 
										new Object[]{ dpr.dimensionKey, dpr.attributeKey, queryRequest.factTable }));
					}
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
				criteria.toArray(new Criterion[]{}),
				root, queryRoot);
	}
	
	protected boolean assertBeanExistance(String name, Class<?> clazz) {
		
		if(beanFactory.containsBean(name)) {
			return beanFactory.isTypeMatch(name, clazz);
		}
		
		return false;
	}
	
	protected static class QueryRequest {
		
		public String factTable;
		
		public FactSelectionRequest[] factSelections;
		
		public DimensionCriterionRequest[] dimensionCriteria;
		
		public FactCriterionRequest[] factCriteria;
		
		public DrillPathRequest[] drillPath;
	}
	
	protected static class FactSelectionRequest {
		
		public String factKey;
		
		public String function;
	}
	
	protected static class DimensionCriterionRequest {
		
		public String dimensionKey;
		
		public String attributeKey;
		
		public String filterValue;
	}
	
	protected static class FactCriterionRequest {
		
		public String factKey;
		
		public String lowerBound;
		
		public boolean isLowerBoundInclusive;
		
		public String upperBound;
		
		public boolean isUpperBoundInclusive;
	}

	protected static class DrillPathRequest {
		
		public String dimensionKey;
		
		public String attributeKey;
		
		public boolean queryRoot;
		
		public String filterValue;
		
	}
}
