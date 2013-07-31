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
	
	/*
	 	<bean id="m110.warehouse.ncaab.messageSource" 
		class="org.springframework.context.support.ResourceBundleMessageSource"
		p:basename="com.minus110.modeling.i18n.warehouse_ncaab" />
		
	<bean id="evf.warehouse.ncaab.messageSourceAccessor" 
		class="org.springframework.context.support.MessageSourceAccessor">
		<constructor-arg ref="m110.warehouse.ncaab.messageSource" />
	</bean>
	 */
		
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
		
		String rootId = element.getAttribute("id");
		Element messageSource = DomUtils.getChildElementByTagName(element, "message-source");
		Element queryEngine = DomUtils.getChildElementByTagName(element, "query-engine");
		
		// Message Source & Accessor
		BeanDefinitionBuilder messageSourceBuilder = 
				BeanDefinitionBuilder.rootBeanDefinition(ResourceBundleMessageSource.class);
		messageSourceBuilder.addPropertyValue("baseName", messageSource.getAttribute("baseName"));
		
		BeanDefinition messageSourceBean = register(
				messageSourceBuilder, String.format("%s.messageSource", rootId), parserContext);
		
		BeanDefinitionBuilder messageSourceAcessorBuilder = 
				BeanDefinitionBuilder.rootBeanDefinition(MessageSourceAccessor.class);
		messageSourceBuilder.addConstructorArgValue(messageSourceBean);
		
		BeanDefinition messageSourceAccessorBean = register(
				messageSourceAcessorBuilder, String.format("%s.messageSourceAccessor", rootId), parserContext);
		
		// Dialect
		BeanDefinition dialectBean = register(
				BeanDefinitionBuilder.rootBeanDefinition(queryEngine.getAttribute("dialect")), 
				String.format("%s.dialect", rootId),
				parserContext); 
		
		// Query Engine
		BeanDefinitionBuilder engineBuilder = 
				BeanDefinitionBuilder.rootBeanDefinition(QueryEngineImpl.class);
		engineBuilder.addConstructorArgValue(dialectBean);
		
		BeanDefinition engineBean = register(
				BeanDefinitionBuilder.rootBeanDefinition(queryEngine.getAttribute("dialect")), 
				String.format("%s.engine", rootId),
				parserContext);
		
		// Dimension Tables
		for(Element dimTableElement : DomUtils.getChildElementsByTagName(element, "dimension-table")) {
			
		}
		
		// Fact Tables
		for(Element factTableElement : DomUtils.getChildElementsByTagName(element, "fact-table")) {
			
			String tableName = factTableElement.getAttribute("table");
			String nameKey = determineNameKey(factTableElement, String.format("factTable.%s.name", tableName));
			String descKey = determineDescriptionKey(factTableElement, String.format("factTable.%s.description", tableName)); 
							
			BeanDefinitionBuilder factTableBuilder = 
					BeanDefinitionBuilder.rootBeanDefinition(FactTableImpl.class);
			factTableBuilder.addConstructorArgValue(messageSourceAccessorBean);
			factTableBuilder.addConstructorArgValue(nameKey);
			factTableBuilder.addConstructorArgValue(descKey);
			factTableBuilder.addConstructorArgValue(engineBean);
			factTableBuilder.addConstructorArgValue(tableName);
			
			BeanDefinition factTableBean = 
					register(factTableBuilder, String.format("%s.factTable.%s", rootId, tableName), parserContext);
			
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
				parseFactCategories(categoryElements, rootId, tableName, messageSourceAccessorBean, factTableBean, parserContext);
				
			} else { //No categories - just the facts
				parseFacts(factTableElement, rootId, tableName, messageSourceAccessorBean, factTableBean, parserContext);	
			}
		}
		
		return engineBean;
	}
	
	protected BeanDefinition register(BeanDefinitionBuilder builder, String id, ParserContext parserContext) {
		BeanDefinition definition = builder.getBeanDefinition();
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
	
	protected void parseFactCategories(List<Element> categoryElements, String rootId, String tableName, BeanDefinition messageSourceAccessorBean, 
			BeanDefinition factTableBean, ParserContext parserContext) throws Exception {
		
		for(Element category : categoryElements) {
			
			// TODO build default value
			String categoryName = category.getAttribute("progammaticName");
			
			String nameKey = determineNameKey(category, String.format("factTable.%s.category.%s.name", tableName, categoryName));
			String descKey = determineDescriptionKey(category, String.format("factTable.%s.category.%s.description", tableName, categoryName));
			
			BeanDefinitionBuilder factCategoryBuilder = BeanDefinitionBuilder.rootBeanDefinition(FactCategoryImpl.class);
			factCategoryBuilder.addConstructorArgValue(messageSourceAccessorBean);
			factCategoryBuilder.addConstructorArgValue(nameKey);
			factCategoryBuilder.addConstructorArgValue(descKey);
			factCategoryBuilder.addConstructorArgValue(factTableBean);
			
			BeanDefinition factCategoryBean = 
					register(factCategoryBuilder, String.format("%s.factTable.%s.category.%s", rootId, tableName, categoryName), parserContext);
			
			// Facts
			parseFacts(category, rootId, tableName, messageSourceAccessorBean, factCategoryBean, parserContext);
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void parseFacts(Element parentElement, String rootId, String tableName, BeanDefinition messageSourceAccessorBean, 
			BeanDefinition parentBean, ParserContext parserContext) throws Exception {
		
		for(Element factElement : DomUtils.getChildElementsByTagName(parentElement, "fact")) {
		
			String columnName = factElement.getAttribute("column");
			String nameKey = determineNameKey(factElement, 
					String.format("factTable.%s.fact.%s.name", tableName, columnName));
			String descKey = determineDescriptionKey(factElement, 
					String.format("factTable.%s.fact.%s.description", tableName, columnName)); 
				
			BeanDefinitionBuilder factBuilder = 
					BeanDefinitionBuilder.rootBeanDefinition(FactImpl.class);
			factBuilder.addConstructorArgValue(messageSourceAccessorBean);
			factBuilder.addConstructorArgValue(nameKey);
			factBuilder.addConstructorArgValue(descKey);
			factBuilder.addConstructorArgValue(
					ClassUtils.forName(getAttributeOrDefaultValue(factElement, "dataType", "java.lang.Integer")));
			factBuilder.addConstructorArgValue(parentBean);
			factBuilder.addConstructorArgValue(columnName);
			
			register(factBuilder, 
					String.format("%s.factTable.%s.fact.%s", rootId, tableName, columnName), parserContext);
		}
	}
}
