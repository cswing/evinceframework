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
	'dojo/dnd/common',
	'dojo/dnd/Source',
	'dojo/fx',
	'dojo/query',
	'dijit/_Container',
	'dijit/_TemplatedMixin',
	'evf/ComplexWidget',
	'./DrillPathConfigurationModel',
	'dojo/i18n!./nls/configuration'
], function(array, declare, lang, dnd, _DndSource, fx, query, Container, Templated, ComplexWidget,
		DrillPathConfigurationModel, i18n) {

	var modelTypeKeys = DrillPathConfigurationModel.prototype.modelTypeKeys;

	var DndSource = declare([_DndSource], {

		tree: null,

		rootItem: null,

		endPointItem: null,

		onDrop: function(source, nodeList, copy) {
			this.inherited(arguments);

			this.tree._updateLayoutOnDrop(source, nodeList, copy);
			this._updateModel(source, nodeList, copy);
		},

		_updateModel: function(source, nodeList/*, copy*/) {
			
			if(nodeList.length > 1)
				throw 'Only a single node being dropped is supported.';

			if(nodeList.length === 0)
				return;

			var node = nodeList[0],
				item = source.getItem(node.id).data;

			if(this.current) {
				
				var currentItem = this.getItem(this.current.id).data;
				if(this.before){
					currentItem = currentItem.prev;
				}

				if (item.next) {
					// remove item from current position
					item.prev.next = item.next;
					item.next.prev = item.prev;
					item.prev = null;
					item.next = null;
				}

				// add item to new position
				var temp = currentItem.next;
				item.next = temp;
				temp.prev = item;

				currentItem.next = item;
				item.prev = currentItem;

			}else{
				// dnd/Source will append to the end of the container,
				// so use the endpoint (which) is out of the container as 
				// the reference item when addiing the new item.
				var prev = this.endPointItem.prev;
				item.next = this.endPointItem;
				item.prev = prev;
				prev.next = item;
			}
		}
	});

	return declare('evfData.pivot.DrillPathConfigurationTree', [ComplexWidget, Templated], {

		templateString: '<div class="evfDataDrillPathConfigurationTree"><ul class="top" data-dojo-attach-point="dndNode"></ul><ul class="bottom" data-dojo-attach-point="endPointNode"></ul></div>',

		dndTemplate: '<li id="{0}" class="{1}"><div class="dijitInline padding"></div><div class="dijitInline icon"></div><span>{2}</span></li>',

		model: null,

		query: null,

		_nodeCache: null,

		rootItem: null,

		endPointItem: null,

		unitOfPadding: 16,

		postMixInProperties: function() {
			this.inherited(arguments);
			
			this._nodeCache = {};
		},

		postCreate: function() {
			this.inherited(arguments);

			this.dndSource = new DndSource(this.dndNode, {
				accept: [modelTypeKeys.dimensionalAttr],
				singular: true,
				creator: this.hitch('_itemCreator'),
				tree: this
			});
			// aop doesn't work. argument list was screwed up.  So instead the source
			// calls the methods on the tree.
			//this.aopAfter(this.dndSource, 'onDrop', '_updateLayoutOnDrop');
			//this.aopAfter(this.dndSource, 'onDrop', '_updateModel');

			var result = this.model.store.query(this.query);
			if(result.length === 0)
				throw 'Unknown root';

			if(result.length > 1)
				throw 'Only supports 1 root';

			this.rootItem = this.dndSource.rootItem = result[0];

			var data = [],
				curr = this.rootItem.next;

			while(curr !== null) {
				if(curr._type === modelTypeKeys.drillPathEndpoint) {
					this.endPointItem = this.dndSource.endPointItem = curr;
					curr = null;
				} else {
					data.push(curr);
					curr = curr.next;
				}
			}

			this.dndSource.insertNodes(false, data);

			var obj = this._itemCreator(this.endPointItem);
			this.domConstruct.place(obj.node, this.endPointNode);

			this._updateLayout();
		},

		_updateLayoutOnDrop: function(/*source, nodeList, copy*/) {
			this._updateLayout();
		},

		_updateLayout: function(from) {

			var fxs = [],
				fromFound = false,
				fnQueue = function(node, wipeIn) {

					var wipeArgs = {
						node: node,
						duration: fromFound ? 50 : 0
					};

					if(wipeIn) {
						fxs.push(fx.wipeIn(wipeArgs));
					} else {
						fxs.splice(0, 0, fx.wipeOut(wipeArgs));
					}

					if(from && from === node)
						fromFound = true;
				};

			var result = query('.dimensionalAttr', this.dndNode),
				paddingMultiple = 0,
				isClosed = false;

			var fnSetPadding = this.hitch(function(itemNode, pm) {
				query('.padding', itemNode)
					.style('paddingLeft', (pm * this.unitOfPadding) + 'px');
			});

			if(result.length === 0) {
				this.domClass.add(this.dndNode, 'noPaths');
			
			} else {

				this.domClass.remove(this.dndNode, 'noPaths');
				this.dndSource.selectNone();

				result.forEach(function(node){
					
					if(isClosed) {
						fnQueue(node, false);

					} else {
						fnQueue(node, true);
						isClosed = !this.domClass.contains(node, 'expanded');
						fnSetPadding(node, paddingMultiple++);
					}
				}, this);
			}

			if(isClosed) {
				fnQueue(this.endPointNode, false);

			} else {
				fnQueue(this.endPointNode, true);
				fnSetPadding(this.endPointNode, paddingMultiple);
			}
			
			fx.chain(fxs).play();
		},

		_toggleDrillPath: function(obj) {
			this.domClass.toggle(obj.node, 'expanded');
			this._updateLayout(obj.node);
		},

		_itemCreator: function(item/*, hint*/) {
			
			var type = item._type,
				css = [type.replace('VM::evfData.', '')],
				n = this.domConstruct.toDom(
					lang.replace(this.dndTemplate, [ dnd.getUniqueId(), css.join(' '), item.name ]));
			
			var obj = this._nodeCache = {node: n, data: item, type: [type]};

			if(type === modelTypeKeys.dimensionalAttr) {
				
				array.forEach(query('.icon', n), function(iconNode){
					this.listen(iconNode, 'click', function() {
						this._toggleDrillPath(obj);
					});
				}, this);

				this.domClass.add(n, 'expanded');
			}

			return obj;
		}
	});

});