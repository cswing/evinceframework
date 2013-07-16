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
	'dojo/store/Memory',
	'dijit/_TemplatedMixin',
	'dijit/tree/ObjectStoreModel', 
	'dijit/Tree',
	'evf/ComplexWidget',
	'evf/dataType/factory',
	'./util',
	'dojo/text!./templates/DrillPathConfiguration.html',
	'dojo/i18n!./nls/configuration'
], function(declare, MemoryStore, Templated, ObjectStoreModel, Tree, ComplexWidget, dataFactory, pivotUtil, template, i18n) {

	return declare('evf.Data.pivot.DrillPathConfiguration', [ComplexWidget, Templated], {
		
		templateString: template,

		factTable: null,

		postMixInProperties: function() {
			this.inherited(arguments);
			this.i18n = i18n;
		},

		postCreate: function() {
			this.inherited(arguments);

			console.dir();

			var store = new MemoryStore({
				data: this.factTable.dimensions,
				getChildren: function(object){
					// TODO filter out business keys
					return object.table.attributes;
				}
			});

			var treeModel = new ObjectStoreModel({
				store: store,
				query: {},
				getRoot: function(onItem, onError){
					return this.factTable
				}
			});

			this.dimensionTree = this.constructWidget(Tree, {
				model: treeModel
			});

			this.dimensionTree.placeAt(this.treeNode);

		}

	});

});