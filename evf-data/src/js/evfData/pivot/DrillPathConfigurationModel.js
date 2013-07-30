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

	return declare('evfData.pivot.DrillPathConfigurationModel', null, {
		
		modelTypeKeys: {

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
		},

		_buildAvailableDimensions: function() {
			return array.map(this.factTable.dimensions, function(dim) {
				return {
					_type:      this.modelTypeKeys.dimension,
					dimension:  dim,
					name:       dim.name
				};
			}, this);
		},

		getAvailableDimensions: function(/* boolean  forRowDrillPath*/){
			// summary:
			//      returns dimensions that are available to add to the drill path.

			if(!this.availableDimensions) {
				this.availableDimensions = this._buildAvailableDimensions();
			}

			// TODO filter out dimensions that are already used in the query definition.

			return this.availableDimensions;
		},

		getAvailableDimensionalAttributes: function(/* boolean */ forRowDrillPath, /* VM::evfData.dimension */ item){
			// summary:
			//		returns dimensional attributes that are available to add to the drill path.

			// TODO filter out dimensions that are already used in the query definition

			var children = item.availableDimensionAttributes;
			if(!children) {
				children = item.availableDimensionAttributes =
					array.map(item.dimension.table.attributes, function(attr) {
						return {
							_type: this.modelTypeKeys.dimensionalAttr,
							attribute: attr,
							name: attr.name
						};
					}, this);
			}
			return children;
		},

		getFirstDrillPathEntry: function(isRows){
			return isRows === true ? this.queryDefinition.rowDrillPath : this.queryDefinition.columnDrillPath;
		},

		setFirstDrillPathEntry: function(isRows, item){
			
			var previousRoot;
			var changeNotifications = [];

			if (isRows === true) {
				
				previousRoot = this.queryDefinition.rowDrillPath;
				this.queryDefinition.rowDrillPath = item;

			} else {

				previousRoot = this.queryDefinition.columnDrillPath;
				this.queryDefinition.columnDrillPath = item;

			}

			if(item === previousRoot)
				return changeNotifications;

			changeNotifications.push([this.queryDefinition, [item]]);

			item.prev = null;

			if(previousRoot) {
				
				// update drillpath, removing new root.
				var curr = previousRoot.next,
					prev = previousRoot;

				while(curr) {
					if (curr === item) {
						prev.next = curr.next;
						
						changeNotifications.push([prev, [prev.next]]);

						if(curr.next) {
							curr.next.prev = prev;
						}
					}
					
					curr = curr.next;
				}

				item.next = previousRoot;
				previousRoot.prev = item;

				changeNotifications.splice(1,0,[item, [item.next]]);

			} else {
				item.next = null;
			}

			return changeNotifications;
		}

	});

});