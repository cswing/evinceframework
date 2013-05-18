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
 define([
 	'dojo/_base/declare',
	'dojo/_base/lang',
	'dijit/_TemplatedMixin',
	'dijit/form/CheckBox',
	'./_DataTypeFormMixin',
	'./factory',
	'dojo/text!./templates/_BoolFieldConfiguration.html',
	'dojo/i18n!./nls/dataTypes'
], function(declare, lang, Templated, CheckBox, DataTypeFormMixin, factory, template, i18n) {
	
	var FieldConfigurationClass = declare([DataTypeFormMixin, Templated], {

		templateString: template,

		field: null,

		_defaultValues: {			
			defaultValue: 	true
		},

		postCreate: function(){
			this.inherited(arguments);
			
			this.defaultValueTextBox = dataType.createWidget(this.defaultValueNode, {
				name: 	'defaultValue'
			});
			this.registerWidgetForStartup(this.defaultValueTextBox);
		},

		startup: function() {
			if(this._started) return;
			this.inherited(arguments);

			this.set('field', this.field || {});
		},

		_setFieldAttr: function(field) {
			this.field = field;
			if(!this._started) return;

			var data = this.mixinDefaults(field);
			this.defaultValueTextBox.set('value', data.defaultValue);
		}
	});

	var dataType = factory.registerDataType({
		key: 			'bool',
		name: 			i18n.bool, 
		widgetFactory: 	function(node, context) {
			var prms = {
				checked: 	context.value
			};
			return new CheckBox(prms, node);
		},
		fieldConfigurationClass: FieldConfigurationClass
		// formatter: // TODO create a formatter for values to be Yes/No
	});

	return dataType;
});