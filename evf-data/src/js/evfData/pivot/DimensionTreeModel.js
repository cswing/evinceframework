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
	'dojo/_base/declare',
	'dojo/_base/lang',
	'dijit/tree/model'
], function(declare, lang, model) {

	return declare('evfData.pivot.DimensionTreeModel', null, {
		// summary: 
		//		an implementation of dijit/tree/model

		factTable: null,

		_implMap: {

			'evfData.factTable': {
				getChildren: function(item, onComplete) {
					onComplete(item.dimensions);
				},
				mayHaveChildren: function() {
					return true;
				},
				getLabel: function(item) {
					return item.name;
				}
			},

			'evfData.dimension': {
				// TODO filter out business keys???
				getChildren: function(item, onComplete) {
					onComplete(item.table.attributes);
				},
				mayHaveChildren: function() {
					return true;
				},
				getLabel: function(item) {
					return item.name;
				}
			},

			'evfData.dimensionAttr': {
				getChildren: function(item, onComplete) {
					onComplete(null);
				},
				mayHaveChildren: function() {
					return false;
				},
				getLabel: function(item) {
					return item.name;
				}
			}
		},

		constructor: function(/* Object */ args){
			lang.mixin(this, args);
		},

		_executeFunction: function(type, fnName, args) {
			var type = this._implMap[type];
			if(type == null)
				throw 'Unknown type: ' + type;

			var fn = type[fnName];

			return fn ? fn.apply(this, args) : null;
		},

		getRoot: function(onItem){
			console.dir(this.factTable);
			onItem(this.factTable);
		},

		mayHaveChildren: function(item){
			return item && this._executeFunction(item['_type'], 'mayHaveChildren', arguments);
		},

		getChildren: function(parentItem, onComplete){
			return parentItem && this._executeFunction(parentItem['_type'], 'getChildren', arguments);	
		},

		isItem: function(something){
			var value = something && this._executeFunction(something['_type'], 'isItem', arguments);

			// null means isItem is not implemented, assume true in such case
			return value === null ? true : value;
		},

		fetchItemByIdentity: function(keywordArgs){
			// summary:
			//		Given the identity of an item, this method returns the item that has
			//		that identity through the onItem callback.  Conforming implementations
			//		should return null if there is no item with the given identity.
			//		Implementations of fetchItemByIdentity() may sometimes return an item
			//		from a local cache and may sometimes fetch an item from a remote server.
			// tags:
			//		extension
		},

		getIdentity: function(item){
			// summary:
			//		Returns identity for an item
			// tags:
			//		extension
		},

		getLabel: function(item){
			return item && this._executeFunction(item['_type'], 'getLabel', arguments);	
		},


		newItem: function(args, parent, insertIndex, before){},

		pasteItem: function(childItem, oldParentItem, newParentItem, bCopy, insertIndex, before){},

		onChange: function(item){
			// summary:
			//		Callback whenever an item has changed, so that Tree
			//		can update the label, icon, etc.   Note that changes
			//		to an item's children or parent(s) will trigger an
			//		onChildrenChange() so you can ignore those changes here.
			// item: dojo/data/Item
			// tags:
			//		callback
		},

		onChildrenChange: function(parent, newChildrenList){
			// summary:
			//		Callback to do notifications about new, updated, or deleted items.
			// parent: dojo/data/Item
			// newChildrenList: dojo/data/Item[]
			// tags:
			//		callback
		},

		destroy: function(){}
		
	});

});