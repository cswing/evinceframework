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
	'dojo/when',
	'dijit/_Container',
	'dijit/_TemplatedMixin',
	'evf/ComplexWidget',
	'evf/dataType/factory',
	'./DimensionTree',
	'./ConfigurationModel',
	'./DrillPathTree',
	'./util',
	'dojo/text!./templates/DrillPathConfiguration.html',
	'dojo/i18n!./nls/configuration'
], function(array, declare, lang, DeferredList, when, _Container, Templated, ComplexWidget, dataFactory,
		DimensionTree, ConfigurationModel, DrillPathTree, pivotUtil, template, i18n) {

	var modelTypeKeys = ConfigurationModel.prototype.modelTypeKeys;

	return declare('evfData.pivot.DrillPathConfiguration', [ComplexWidget, Templated], {
		
		templateString: template,

		configModel: null,

		isRows: null,

		postMixInProperties: function() {
			this.inherited(arguments);
			this.i18n = i18n;
		},

		postCreate: function() {
			this.inherited(arguments);

			this.dimensionTree = this.constructWidget(DimensionTree, {
				model: this.configModel,
			}, this.dimensionTreeNode);

			this.drillPathTree = this.constructWidget(DrillPathTree, {
				model: this.configModel,
				query: { _type: modelTypeKeys.drillPathRoot, isRows: this.isRows }
			}, this.drillPathTreeNode);
		},

		resize: function(cb){
			
			if(!cb) return;

			this.domStyle.set(this.domNode, 'height', cb.h + 'px');

			var dim_mb = this.domGeom.getMarginBox(this.dimensionNode),
			  dimcn_mb = this.domGeom.getMarginBox(this.dimensionContentNode),
			  dp_mb = this.domGeom.getMarginBox(this.drillPathNode),
			  dpcn_mb = this.domGeom.getMarginBox(this.drillPathContentNode);
			
			this.domStyle.set(this.dimensionTree.dndNode, 'height', 
				(cb.h - (dim_mb.h - dimcn_mb.h)) + 'px');
			this.domStyle.set(this.drillPathTree.domNode, 'height', 
				(cb.h - (dp_mb.h - dpcn_mb.h)) + 'px');

			this.domStyle.set(this.drillPathTree.domNode, 'width', (cb.w - dim_mb.w) + 'px');
		}

	});

});