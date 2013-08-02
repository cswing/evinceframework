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
package com.evinceframework.data.spring.xml;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.evinceframework.data.warehouse.impl.FactCategoryImpl;
import com.evinceframework.data.warehouse.impl.FactImpl;
import com.evinceframework.data.warehouse.impl.FactTableImpl;
import com.evinceframework.data.warehouse.query.impl.QueryEngineImpl;

public class WarehouseBeanDefinitionParser implements BeanDefinitionParser {
			
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		
		try {
			return doParse(element, parserContext);
			
		} catch (Exception ex) {
			parserContext.getReaderContext().error(ex.getMessage(), element);
			return null;
		}
	}
	
	protected BeanDefinition doParse(Element element, ParserContext parserContext) throws Exception {
		
		Element messageSource = DomUtils.getChildElementByTagName(element, "message-source");
		Element queryEngine = DomUtils.getChildElementByTagName(element, "query-engine");
		BeanNames beanNames = new BeanNames(element.getAttribute("id"));
		
		// Query Engine
		BeanDefinition engineBean = register(BeanDefinitionBuilder.genericBeanDefinition(
				queryEngine.getAttribute("dialect")), beanNames.getDialect(), parserContext);
		
		BeanDefinitionBuilder engineBuilder = BeanDefinitionBuilder.genericBeanDefinition(QueryEngineImpl.class);
		engineBuilder.addConstructorArgReference(beanNames.getDialect());		
		register(engineBuilder, beanNames.getEngine(), parserContext);
		
		// Message Source & Accessor
		BeanDefinitionBuilder messageSourceBuilder = 
				BeanDefinitionBuilder.genericBeanDefinition(ResourceBundleMessageSource.class);
		messageSourceBuilder.addPropertyValue("basename", messageSource.getAttribute("baseName"));
		register(messageSourceBuilder, beanNames.getMessageSource(), parserContext);
				
		BeanDefinitionBuilder messageSourceAccessorBuilder = 
				BeanDefinitionBuilder.genericBeanDefinition(MessageSourceAccessor.class);
		messageSourceAccessorBuilder.addConstructorArgReference(beanNames.getMessageSource());
		register(messageSourceAccessorBuilder, beanNames.getMessageSourceAccessor(), parserContext);
		
		// Dimension Tables
//		for(Element dimTableElement : DomUtils.getChildElementsByTagName(element, "dimension-table")) {
//			
//		}
		
		// Fact Tables
		for(Element factTableElement : DomUtils.getChildElementsByTagName(element, "fact-table")) {
			
			String tableName = factTableElement.getAttribute("table");
			String nameKey = determineNameKey(factTableElement, String.format("factTable.%s.name", tableName));
			String descKey = determineDescriptionKey(factTableElement, String.format("factTable.%s.description", tableName)); 
							
			BeanDefinitionBuilder factTableBuilder = 
					BeanDefinitionBuilder.rootBeanDefinition(FactTableImpl.class);
			factTableBuilder.addConstructorArgReference(beanNames.getMessageSourceAccessor());
			factTableBuilder.addConstructorArgValue(nameKey);
			factTableBuilder.addConstructorArgValue(descKey);
			factTableBuilder.addConstructorArgReference(beanNames.getEngine());
			factTableBuilder.addConstructorArgValue(tableName);
			
			register(factTableBuilder, beanNames.buildTable(tableName), parserContext);
			
			// Dimensions
			/*
			 <bean id="m110.warehouse.ncaab.factTable.teamGameStatLine.dimension.date"
					class="com.evinceframework.data.warehouse.impl.DimensionImpl">
				<constructor-arg index="0" ref="m110.warehouse.dimensionTable.date" />
				<constructor-arg index="1" ref="m110.warehouse.ncaab.factTable.teamGameStatLine" />
				<constructor-arg index="2" value="dim_dateId" />
			</bean>
			 * */
			
			// Fact Categories
			List<Element> categoryElements = DomUtils.getChildElementsByTagName(element, "fact-category");
			
			if(categoryElements.size() > 0) {
				parseFactCategories(categoryElements, tableName, parserContext, beanNames);
				
			} else { //No categories - just the facts
				parseFacts(factTableElement, tableName, beanNames.buildTable(tableName), parserContext, beanNames);	
			}
		}
		
		return engineBean;
	}
	
	protected BeanDefinition register(BeanDefinitionBuilder builder, String id, ParserContext parserContext) {
		
		BeanDefinition definition = builder.getBeanDefinition();
		definition.setScope(BeanDefinition.SCOPE_SINGLETON);
		BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id, new String[]{});
		
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, parserContext.getRegistry());
		BeanComponentDefinition componentDefinition = new BeanComponentDefinition(holder);
		//postProcessprotectedComponentDefinition(componentDefinition);
		parserContext.registerComponent(componentDefinition);
		
		return definition;
	}

	protected String determineNameKey(Element ele, String defaultValue) {
		return getAttributeOrDefaultValue(ele, "nameKey", defaultValue);
	}

	protected String determineDescriptionKey(Element ele, String defaultValue) {
		return getAttributeOrDefaultValue(ele, "nameKey", defaultValue);
	}

	protected String getAttributeOrDefaultValue(Element ele, String attrName, String defaultValue) {
		return ele.hasAttribute(attrName) ? ele.getAttribute(attrName) : defaultValue;
	}
	
	protected void parseFactCategories(
			List<Element> categoryElements, String tableName, ParserContext parserContext, BeanNames beanNames) 
					throws Exception {
		
		for(Element category : categoryElements) {
			
			// TODO build default value
			String categoryName = category.getAttribute("progammaticName");
			
			String nameKey = determineNameKey(category, String.format("factTable.%s.category.%s.name", tableName, categoryName));
			String descKey = determineDescriptionKey(category, String.format("factTable.%s.category.%s.description", tableName, categoryName));
			
			BeanDefinitionBuilder factCategoryBuilder = BeanDefinitionBuilder.rootBeanDefinition(FactCategoryImpl.class);
			factCategoryBuilder.addConstructorArgReference(beanNames.getMessageSourceAccessor());
			factCategoryBuilder.addConstructorArgValue(nameKey);
			factCategoryBuilder.addConstructorArgValue(descKey);
			factCategoryBuilder.addConstructorArgReference(beanNames.buildTable(tableName));
			
			String categoryBeanName = beanNames.buildCategory(tableName, categoryName);
			register(factCategoryBuilder, categoryBeanName, parserContext);
			
			// Facts
			parseFacts(category, tableName, categoryBeanName, parserContext, beanNames);
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void parseFacts(Element parentElement, String tableName, String parentBean, 
			ParserContext parserContext, BeanNames beanNames) throws Exception {
		
		for(Element factElement : DomUtils.getChildElementsByTagName(parentElement, "fact")) {
		
			String columnName = factElement.getAttribute("column");
			String nameKey = determineNameKey(factElement, 
					String.format("factTable.%s.fact.%s.name", tableName, columnName));
			String descKey = determineDescriptionKey(factElement, 
					String.format("factTable.%s.fact.%s.description", tableName, columnName)); 
				
			BeanDefinitionBuilder factBuilder = 
					BeanDefinitionBuilder.rootBeanDefinition(FactImpl.class);
			factBuilder.addConstructorArgReference(beanNames.getMessageSourceAccessor());
			factBuilder.addConstructorArgValue(nameKey);
			factBuilder.addConstructorArgValue(descKey);
			factBuilder.addConstructorArgValue(
					ClassUtils.forName(getAttributeOrDefaultValue(factElement, "dataType", "java.lang.Integer")));
			factBuilder.addConstructorArgReference(parentBean);
			factBuilder.addConstructorArgValue(columnName);
			
			register(factBuilder, beanNames.buildFact(tableName, columnName), parserContext);
		}
	}
	
	private class BeanNames {
		
		private String rootId;

		public BeanNames(String rootId) {
			this.rootId = rootId;
		}
		
		public String getDialect() {
			return String.format("%s.dialect", rootId);
		}
		
		public String getEngine() {
			return String.format("%s.engine", rootId);
		}
		
		public String getMessageSource() {
			return String.format("%s.messageSource", rootId);
		}
		
		public String getMessageSourceAccessor() {
			return String.format("%s.messageSourceAccessor", rootId);
		}
		
		public String buildTable(String tableName) {
			return String.format("%s.factTable.%s", rootId, tableName);
		}
		
		public String buildCategory(String tableName, String categoryName) {
			return String.format("%s.factTable.%s.category.%s", tableName, categoryName);
		}
		
		public String buildFact(String tableName, String columnName) {
			return String.format("%s.factTable.%s.fact.%s", rootId, tableName, columnName);
		}
	}
}
