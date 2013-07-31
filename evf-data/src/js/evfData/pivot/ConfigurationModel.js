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
	'dojo/store/Memory',
	'dojo/store/Observable'
], function(array, declare, lang, Memory, Observable) {

	var DrillPathConfigurationStore = declare([Memory], {});

	return declare('evfData.pivot.ConfigurationModel', null, {
		
		modelTypeKeys: {
			fact:               'VM::evfData.fact',
			factSelection:      'VM::evfData.factSelection',
			dimensionRoot:      'VM::evfData.dimensionRoot',
			dimension:          'VM::evfData.dimension',
			dimensionalAttr:    'VM::evfData.dimensionalAttr',
			drillPathRoot:      'VM::evfData.drillPathRoot',
			drillPathEndpoint:  'VM::evfData.drillPathEndpoint',
		},

		factTable: null,

		queryDefinition: null,

		constructor: function(/* Object */ args){
			lang.mixin(this, args);

			this.store = this._buildStore();
		},
		
		_buildStore: function() {

			var dataArray = [], counter = 0;

			// Facts
			array.forEach(this.factTable.facts, function(f) {
				dataArray.push({
					_type: this.modelTypeKeys.fact,
					_fact: f,
					name: f.name,
					selection: null
				});
			}, this);
			
			//Fact Selections
			// TODO

			// Root for dimensions 
			var root = {
				_type: this.modelTypeKeys.dimensionRoot,
				id: this.modelTypeKeys.dimensionRoot + '::' + counter++,
				dimensions: []
			};
			dataArray.push(root);

			// Dimensions
			array.forEach(this.factTable.dimensions, function(dimension) {
				var dim = {
					_type:              this.modelTypeKeys.dimension,
					_dimension:         dimension,
					id:                 this.modelTypeKeys.dimension + '::' + counter++,
					parent:             root,
					name:               dimension.name,
					attributes:         []
				};
				root.dimensions.push(dim);
				dataArray.push(dim);

				// Dimension Attributes
				array.forEach(dimension.table.attributes, function(attribute) {
					var attr = {
							_type:      this.modelTypeKeys.dimensionalAttr,
							_attribute: attribute,
							id:         this.modelTypeKeys.dimensionalAttr + '::' + counter++,
							parent:     dim,
							name:       attribute.name
					};
					dim.attributes.push(attr);
					dataArray.push(attr);

				}, this);
			}, this);

			var fnCreateDrillPath = lang.hitch(this, function(isRows) {
				var drillPath = {
					_type:      this.modelTypeKeys.drillPathRoot,
					isRows:     isRows,
					id:         this.modelTypeKeys.drillPathRoot + '::' + counter++,
					name:       'Drill path',
					next:       null
				};
				dataArray.push(drillPath);

				var drillPathEndpoint = {
					_type:      this.modelTypeKeys.drillPathEndpoint,
					id:         this.modelTypeKeys.drillPathEndpoint + '::' + counter++,
					prev:       drillPath,
					name:       this.factTable.name
				};
				drillPath.next = drillPathEndpoint;
				dataArray.push(drillPathEndpoint);
			});

			// Drill Paths
			fnCreateDrillPath(true);
			fnCreateDrillPath(false);

			return new Observable(
				new DrillPathConfigurationStore({
					data: dataArray
				})
			);
		}

	});
});