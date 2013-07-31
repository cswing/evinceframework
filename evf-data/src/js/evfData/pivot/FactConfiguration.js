/*
 * Copyright 2013 the original author or authors.
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
	'dojo/_base/lang',
	'dojo/DeferredList',
	'dojo/dnd/common',
	'dojo/dnd/Source',
	'dojo/when',
	'dijit/_Container',
	'dijit/_TemplatedMixin',
	'evf/ComplexWidget',
	'evf/dataType/factory',
	'./DrillPathConfigurationModel',
	'./util',
	'dojo/text!./templates/FactConfiguration.html',
	'dojo/i18n!./nls/configuration'
], function(array, declare, lang, DeferredList, dnd, DndSource, when, _Container, Templated, ComplexWidget, dataFactory, ConfigurationModel, pivotUtil, template, i18n) {

	var modelTypeKeys = ConfigurationModel.prototype.modelTypeKeys;

	return declare([ComplexWidget, Templated], {

		templateString: template,

		dndTemplate: '',

		configModel: null,

		postMixInProperties: function() {
			this.inherited(arguments);
			this.i18n = i18n;
		},

		postCreate: function(){
			this.inherited(arguments);

			this.dndSource = new DndSource(this.factContentNode, {
				singular: true,
				copyOnly: true,
				creator: this.hitch('_itemCreator')
			});

			var result = this.configModel.store.query({_type: modelTypeKeys.facts});
			this.dndSource.insertNodes(false, result);
		},

		_itemCreator: function(item/*, hint*/) {
			var type = item._type,
				id = dnd.getUniqueId(),
				n = domConstruct.toDom(lang.replace(this.dndTemplate, [id, type.replace('VM::evfData.', ''), item.name ]));
			
			var obj = {node: n, data: item, type: [type], children: []};

			if(type === modelTypeKeys.dimensionalAttr) {
				
				var parent = this._cache[item.parent];
				if (!parent)
					throw 'Unable to determine parent for this dimensional attribute.';

				parent.children.push(obj);
			}

			return obj;
		}

	});
});