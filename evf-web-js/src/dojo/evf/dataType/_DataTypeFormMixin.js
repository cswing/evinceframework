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
 	'dojo/_base/array',
	'dojo/_base/declare',
	'dijit/form/_FormMixin',
	'evf/ComplexWidget',
	'dojo/i18n!../entity/nls/entity',
], function(array, declare, FormMixin, ComplexWidget, entityI18N) {

	return declare('evf.dataType._DataTypeFormMixin', [ComplexWidget, FormMixin], {
		// summary:
		//		A base mixin for providing a sub form for adding/editing a fields definition 
		//		of a particular data type.

		i18n: null,
		//	the entity i18n 

		postMixInProperties: function() {
			this.inherited(arguments);
			this.i18n = entityI18N;
		},

		postCreate: function(){
			this.inherited(arguments);
			this.containerNode = this.domNode; // _WidgetBase.getChildren() support
		},

		startup: function() {
			if(this._started) return;
			this.inherited(arguments);

			// set default values on the widgets.  reset will use this value.
			if(this._defaultValues) {
				array.forEach(this._getDescendantFormWidgets(), function(w){
					var name = w.get('name');
					if(name && this._defaultValues[name] !== undefined) {
						w._resetValue = this._defaultValues[name];
					}
				}, this);
			}
		},

		mixinDefaults: function(field) {
			
			var defaults = this.dojoLang.clone(this._defaultValues || {}),
				parent = field || { props: {}},
				props = parent['props'] || {};

			return this.dojoLang.mixin(defaults, props || {});
		}
	});
});