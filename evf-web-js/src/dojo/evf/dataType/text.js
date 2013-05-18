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
	'dijit/form/NumberTextBox',
	'dijit/form/ValidationTextBox',
	'./_DataTypeFormMixin',
	'./factory',
	'dojo/text!./templates/_TextFieldConfiguration.html',
	'dojo/i18n!../nls/common',
	'dojo/i18n!./nls/dataTypes'
], function(declare, lang, Templated, CheckBox, NumberTextBox, ValidationTextBox, DataTypeFormMixin, factory, template, commonI18N, dataTypesI18N) {

	var FieldConfigurationClass = declare([DataTypeFormMixin, Templated], {

		templateString: template,

		field: null,

		_defaultValues: {
			required: 		false,
			minLength: 		1,
			maxLength: 		500,
			defaultValue: 	'' 
		},

		postCreate: function(){
			this.inherited(arguments);

			this.requiredField = this.constructWidget(CheckBox, {
				name: 		'required',
				checked: 	'required',
				value: 		'true'
			}, this.requiredNode);

			this.minLengthTextBox = this.constructWidget(NumberTextBox, {
				name: 'minLength',
				required:   true,
				constraints: {
					min:    1,
					max:    500,
					places: 0
				}
			}, this.minLengthNode);

			this.maxLengthTextBox = this.constructWidget(NumberTextBox, {
				name: 'maxLength',
				required:   true,
				constraints: {
					min:    1,
					max:    500,
					places: 0
				}
			}, this.maxLengthNode);

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

		validate: function() {
			return true;
		},

		// TODO validation
		//  - maxLength > minLength
		//  - defaultValue complies with min and max lengths

		_setFieldAttr: function(field) {
			this.field = field;
			if(!this._started) return;

			var data = this.mixinDefaults(field);

			this.requiredField.set('value', data.required);
			this.minLengthTextBox.set('value', data.minLength);
			this.maxLengthTextBox.set('value', data.maxLength);
			this.defaultValueTextBox.set('value', data.defaultValue);
		}
	});

	var dataType = factory.registerDataType({
		key:            'text',
		name:           dataTypesI18N.text,
		fieldConfigurationClass: FieldConfigurationClass,
		widgetFactory:  function(node, context) {
			return new ValidationTextBox(context, node);
		}
	});

	/*=====
	dataType.__Context = declare(null, {
		// value: String?
		// 		the initial value to set on the textbox
		// required: Boolean
		//		user is required to enter data into this field
		// pattern: String
		//		pattern string used to validate the input
	});
	=====*/

	return dataType;
});