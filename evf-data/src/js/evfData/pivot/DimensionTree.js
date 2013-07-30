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
	'dojo/dom-class',
	'dojo/dom-construct',
	'dojo/dom-style',
	'dojo/dnd/common',
	'dojo/dnd/Source',
	'dojo/fx',
	'dojo/query',
	'dijit/_Container',
	'dijit/_TemplatedMixin',
	'evf/ComplexWidget',
	'./DrillPathConfigurationModel',
	'./DrillPathConfigurationTree',
	'dojo/i18n!./nls/configuration'
], function(array, declare, lang, domClass, domConstruct, domStyle, dnd, _DndSource, fx, query, Container, Templated, ComplexWidget,
		DrillPathConfigurationModel, DrillPathConfigurationTree, i18n) {

	var modelTypeKeys = DrillPathConfigurationModel.prototype.modelTypeKeys;

	var DndSource = declare([_DndSource], {
		checkAcceptance: function(/*source, nodes*/){
			return false;
		}
	});

	return declare('evfData.pivot.DimensionTree', [ComplexWidget, Templated], {

		templateString: '<div class="evfDataDimensionTree"><ul data-dojo-attach-point="dndNode"></ul></div>',

		paddingMultiple: 32,

		model: null,

		rootItem: null,

		_cache: null,

		postMixInProperties: function() {
			this.inherited(arguments);
			this._cache = {};
		},

		postCreate: function() {
			this.inherited(arguments);

			this.dndSource = new DndSource(this.dndNode, {
				singular: true,
				copyOnly: true,
				creator: this.hitch('_itemCreator')
			});

			var result = this.model.store.query({_type: modelTypeKeys.dimensionRoot});
			if(result.length === 0)
				throw 'Unknown root';

			if(result.length > 1)
				throw 'Only supports 1 root';

			this.rootItem = result[0];
			
			array.forEach(this.rootItem.dimensions, function(dim) {
				
				var obj = this._cache[dim] =
						this._itemCreator(dim); // not calling insertNodes, because these are not draggable
					
				domConstruct.place(obj.node, this.dndNode);

				array.forEach(query('.icon', obj.node), function(iconNode){
					this.listen(iconNode, 'click', function() {
						this._toggleAttributes(obj);
					});
				}, this);

				var data = [];
				array.forEach(dim.attributes, function(attr) {
					data.push(attr);
				}, this);
			    this.dndSource.insertNodes(false, data);
			    
			    this._toggleAttributes(obj, false);

			}, this);
		},

		_toggleAttributes: function(obj, force) {

			domClass.toggle(obj.node, 'expanded', force);

			var fxs = [],
				expanded = domClass.contains(obj.node, 'expanded');

			array.forEach(obj.children, function(child) {
				
				if(expanded) {
					fxs.push(fx.wipeIn({
						node: child.node,
						duration: 50
					}));
				}else{
					fxs.splice(0, 0, fx.wipeOut({
						node: child.node,
						duration: 50
					}));
				}
			});

			fx.chain(fxs).play();
		},

		_itemCreator: function(item/*, hint*/) {
			var type = item._type,
				id = dnd.getUniqueId(),
				n = domConstruct.toDom(
					lang.replace(DrillPathConfigurationTree.prototype.dndTemplate,
						[id, type.replace('VM::evfData.', ''), item.name ]));
			
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