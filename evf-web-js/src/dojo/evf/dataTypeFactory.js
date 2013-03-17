/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 define([], function() {

    /*=====
    dataTypeFactory.__DataTypeDefinition = declare(null, {
        // key: String
        //      
        // widgetFactory: function
		//		
        // getter: function?
		//		
        // setter: function?
		//		
        // formatter: function?
		//		
    });
    =====*/

    /*=====
    dataTypeFactory.__DataType = declare(null, {
        // key: String
        //      
        // createWidget: function
		//		
        // getValue: function
		//		
        // setValue: function?
		//		
        // format: function?
		//		
    });
    =====*/

    var factory = dojo.getObject("evf.dataTypeFactory", true);
    
    factory._dataTypeMap = {};
    
    factory._defaultFormatter = function(data) { return data; };
    factory._defaultGetter = function(widget) { return widget.get('value'); };
    factory._defaultSetter = function(widget, value) { return widget.set('value', value); };
    
    factory.registerDataType = function(/*dataTypeFactory.__DataTypeDefinition*/dataType, replaceIfExists) {
        // summary:
        //      registers and returns a dataTypeFactory.__DataType object.
        //  returns
        //      the registered dataTypeFactory.__DataType
    
        if(!dataType) throw 'dataType is unknown';
    
        if (factory._dataTypeMap[dataType.key] && !replaceIfExists)
            throw dataType.key + ' already exists as a dataType.';
        
        if(!dataType.widgetFactory)
            throw dataType.key + ' - a widgetFactory is required.';
        
        factory._dataTypeMap[dataType.key] = {
            createWidget:   dataType.widgetFactory,
            getValue:       dataType.getter || factory._defaultGetter,
            setValue:       dataType.setter || factory._defaultSetter,
            format:         dataType.formatter || factory._defaultFormatter
        };
        return factory._dataTypeMap[dataType.key];
    };
    
    factory.findDataType = function(key) {
        var dt = factory._dataTypeMap[key];
        if (!dt)
            throw key + ' is NOT a known dataType';
        return dt;
    }
    
    factory.createWidget = function(key, node, context) {
        return factory.findDataType(key).createWidget(node, context);
    };
    
    factory.format = function(key, data) {
        return factory.findDataType(key).format(data);
    }
    
    return factory;
});