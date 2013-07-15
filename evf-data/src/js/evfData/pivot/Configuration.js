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
	'dijit/_TemplatedMixin',
	'evf/ComplexWidget',
	'evf/dataType/factory',
	'evf/dialog/SimpleFormDialog',
	'evf/dialog/util',
	'./Configuration',
	'dojo/text!./templates/Configuration.html',
	'dojo/i18n!./nls/configuration'
], function(declare, Templated, ComplexWidget, dataFactory, SimpleFormDialog, dialogUtil, Configuration, template, i18n) {

	var defaultQueryDefinition = {

		name: '',

		rowSummarizations: [],
		columnSummarizations: [],

		summarizations: [],
		factCriterion: [],
		factSelections: []
	};

	return declare('evfData.pivot.Configuration', [ComplexWidget, Templated], {

		templateString: template,

		factTable: null,

		queryDefinition: null,

		nameConstraints: {
			maxLength: 50
		},

		postMixInProperties: function() {
			this.inherited(arguments);

			this.i18n = i18n;
			this.queryDefinition = this.dojoLang.clone(this.queryDefinition) 
				|| this.dojoLang.mixin({factTable: this.factTable}, defaultQueryDefinition);


			/*
			if (this.factTable.dimensions.length < 2) {
				dialogUtil.showErrorMessage(this.dojoLang.replace(i18n.error_notEnoughDimensions, [this.factTable.name]));
			}
			*/
			//this.columns = [];
			this.availableDimensions = [];

			// row sumarizations - determine how the rows are summarized
			if (this.queryDefinition.rowSummarizations.length > 0) {



			} else { // default to the first dimension
				var defaultSummarization = this.factTable.dimensions[0];
				
			}

			if (this.queryDefinition.columnSummarizations.length > 0) {

			} else { // default to the second dimension

				var defaultSummarization = this.factTable.dimensions[1];
				

			}
			
		},

		postCreate: function() {
			this.inherited(arguments);

			this.nameTextBox = dataFactory.createWidget('text', this.nameNode, {
				required: true,
				constraints: this.nameConstraints
			}, this.queryDefinition, 'name');

			this.domHtml.set(this.factTableNode, this.factTable.name);

			//factTableSelectNode
			//tabContainerNode

			//console.dir(this.factTable);
			//this.domHtml.set(this.domNode, 'PivotGrid...');
		}

	});
});