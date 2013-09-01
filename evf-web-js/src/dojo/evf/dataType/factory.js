/*
 * Copyright 2012-3 Craig Swing.
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
 define([
 	'dojo/_base/lang',
 	'dojo/on',
	'dojo/store/Memory',
	'evf/dataType/_Binding'
], function(lang, on, Memory, DefaultBinding) {

	/*=====
	evf.dataType.__DataTypeDefinition = declare(null, {
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
		// fieldConfigurationClass: ctor
		//
	});
	=====*/

	/*=====
	evf.dataType.__DataType = declare(null, {
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
		// fieldConfigurationClass: ctor
		//
	});
	=====*/

	var exports = {
		// summary:
		//		A factory object used to register data types and instantiate widgets based on data types.

		store: new Memory({ data:[] })
	};

	exports._dataTypeMap = {};

	exports._defaultFormatter = function(data) { return data; };
	exports._defaultGetter = function(widget) { return widget.get('value'); };
	exports._defaultSetter = function(widget, value) { return widget.set('value', value); };
	exports._defaultBinder = function(widget, dataObject, bindingKey, observable) {
		return new DefaultBinding({
			widget: widget, 
			observable: observable, 
			dataObject: dataObject, 
			bindingKey: bindingKey
		});
	};

	exports.registerDataType = function(/*dataType.__DataTypeDefinition*/dataType, replaceIfExists) {
		// summary:
		// 		registers and returns a dataTypeexports.__DataType object.
		// returns
		// 		the registered dataTypeexports.__DataType

		if(!dataType) throw 'dataType is unknown';

		if (exports._dataTypeMap[dataType.key] && !replaceIfExists)
			throw dataType.key + ' already exists as a dataType.';

		if(!dataType.widgetFactory)
			throw dataType.key + ' - a widgetFactory is required.';

		var data = {
			id: 			dataType.key,
			name: 			dataType.name || dataType.key,
			createWidget: 	dataType.widgetFactory,
			getValue: 		dataType.getter || exports._defaultGetter,
			setValue: 		dataType.setter || exports._defaultSetter,
			format: 		dataType.formatter || exports._defaultFormatter,
			binder: 		dataType.binder || exports._defaultBinder,
			fieldConfigurationClass: dataType.fieldConfigurationClass
		};

		exports.store.add(data);

		return data;
	};

	exports.findDataType = function(key) {
		var results = exports.store.query({ id: key });
		if(results.length < 1)
			throw key + ' is NOT a known dataType';

		return results[0];
	}

	exports.createWidget = function(key, node, context, dataObject, bindingKey, observable) {

		var dataType = exports.findDataType(key);

		var params = lang.mixin({
			value: lang.getObject(bindingKey, false, dataObject)
		}, context || {});
		var widget = dataType.createWidget(node, params);

		var requiresBinding = dataObject && bindingKey;
		if(requiresBinding) {
			widget.binding = dataType.binder(widget, dataObject, bindingKey, observable);
		}

		return widget;
	};

	exports.format = function(key, data) {
		return exports.findDataType(key).format(data);
	}

	return exports;
});