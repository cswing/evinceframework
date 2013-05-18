/*
 * Copyright 2013 Craig Swing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0<div>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 define([
	'dojo/_base/array',
	'dojo/_base/declare',
	'dijit/_TemplatedMixin',
	'dijit/form/_FormMixin',
	'dijit/form/FilteringSelect',
	'dijit/form/ValidationTextBox',
	'dijit/layout/ContentPane',
	'dijit/layout/StackContainer',
	'evf/ComplexWidget',
	'evf/dataType/factory',
	'dojo/text!./templates/FieldForm.html',
	'dojo/i18n!./nls/entity',
], function(array, declare, Templated, FormMixin, FilteringSelect, ValidationTextBox, ContentPane, 
		StackContainer, ComplexWidget, dataTypeFactory, template, i18n) {

	return declare('evf.entity.FieldForm', [ComplexWidget, Templated, FormMixin], {

		templateString: template,

		_configHash: null,

		field: null,

		postMixInProperties: function() {
			this.inherited(arguments);
			this.i18n = i18n;
			this._configHash = {};
		},

		postCreate: function() {
			this.inherited(arguments);

			this.containerNode = this.domNode; //required for FormMixin

			this.nameTextBox = this.constructWidget(ValidationTextBox, {
				name: 'name',
				required: true,
				maxLength: 50,
			}, this.nameNode);

			this.dataTypeSelect = this.constructWidget(FilteringSelect, {
				name: 'dataType',
				required: true,
				store: 			dataTypeFactory.store,
				searchAttr: 	'name'
			}, this.dataTypeNode);

			this.stack = this.constructWidget(StackContainer, {'style': 'height: 300px;'}, this.stackNode);
			this.defaultPane = new ContentPane({ content: '' });
			this.stack.addChild(this.defaultPane);
		},

		reset: function(){
			this.inherited(arguments);

			// reset all children of the stack.
			array.forEach(this.stack.getChildren(), function(child) {
				array.forEach(this._getDescendantFormWidgets(child), function(widget){
					if(widget.reset){
						widget.reset();
					}
				}, this);
			}, this);

			this.defaultPane.set('content', '');
			this.stack.selectChild(this.defaultPane);
		},

		validate: function() {
			var res = this.inherited(arguments);

			if(this.nameTextBox.isValid()) {
				// validate that there are no other names 
				//	- name - unique accross all fields of this entity
			}
			
			return res && (!this.stack.selectedChildWidget.validate || this.stack.selectedChildWidget.validate());
		},

		_setFieldAttr: function(field) {
			var isAddMode = !field;
			this.field = field || {};
			if(!this._started) return;

			this.reset();

			this.nameTextBox.set('value', this.field.name);
			this.dataTypeSelect.set('value', this.field.dataType);
			this.dataTypeSelect.set('disabled', !isAddMode);
			
			if(this.stack.selectedChildWidget.field != this.field)
				this.stack.selectedChildWidget.set('field', this.field);
		},

		_getDescendantFormWidgets: function(/*dijit/_WidgetBase[]?*/ children){
			// override FormMixin's implementation to only return formvalues in the selected schild of the stack container.

			// if children are specified, then the FormMixin implementation will work
			if(children) {
				return this.inherited(arguments);
			}

			// If children are not specified, then the default children 
			var res = [
				this.nameTextBox,
				this.dataTypeSelect
			];

			if(this.stack.selectedChildWidget && this.stack.selectedChildWidget.getChildren)
				res = res.concat(this._getDescendantFormWidgets(this.stack.selectedChildWidget.getChildren()));

			return res;
		},

		startup: function() {
			if(this._started) return;
			this.inherited(arguments);

			this.listen(this.dataTypeSelect, 'change', '_onDataTypeChange');
			this.set('field', this.field);
		},

		_onDataTypeChange: function(newId) {

			var sub = this._configHash[newId];

			if (!sub) {
					
				var itm = dataTypeFactory.store.get(newId);

				if (itm && itm.fieldConfigurationClass) {
					var Wctor = this.ensureCtor(itm.fieldConfigurationClass);
					this._configHash[newId] = sub = new Wctor({
						field: this.field
					});
					this.stack.addChild(sub);

				} else {
					sub = this.defaultPane;
					this.defaultPane.set('content', this.i18n.addFieldDialog_msg_noDataTypeConfiguration);
				}
			}

			if(sub.field != this.field)
				sub.set('field', this.field);

			this.stack.selectChild(this._configHash[newId]);
			this.stack.resize();
		}

	});
});