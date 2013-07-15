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
	'dgrid/Grid', 
	'dgrid/extensions/DijitRegistry',
	'evf/ComplexWidget',
	'evf/_UtilMixin',
	'evf/dialog/_FormDialogMixin',
	'evf/dialog/util',
	'./pivot/Configuration',
	'dojo/text!./templates/PivotGrid.html',
	'dojo/i18n!./nls/pivot'
], function(declare, Templated, Grid, DijitRegistry, ComplexWidget, UtilMixin, FormDialogMixin, dialogUtil, Configuration, template, i18n) {

	var InternalGrid = declare([Grid, DijitRegistry, UtilMixin], {});

	var defaultQuery = {

		rowSummarizations: [],
		columnSummarizations: [],


		summarizations: [],
		factCriterion: [],
		factSelections: []
	};

	var ConfigurationDialog = declare([FormDialogMixin], {

		contentParams: null,

		createContent: function(node) {
			return new Configuration(this.contentParams, node);
		},

		onSubmit: function() {
			console.dir(this.content.queryDefinition);

			return true;
		}

	});

	return declare('evfData.PivotGrid', [ComplexWidget, Templated], {

		templateString: template,

		factTable: null,

		query: null,

		postMixInProperties: function() {
			this.inherited(arguments);
			this.query = this.query || this.dojoLang.mixin({}, defaultQuery);

			//factSelectionNode
			//hDimSelectionNode


			if (this.factTable.dimensions.length < 2) {
				dialogUtil.showErrorMessage(this.dojoLang.replace(i18n.error_notEnoughDimensions, [this.factTable.name]));
			}

			this.columns = [];
			this.availableDimensions = [];

			// row sumarizations - determine how the rows are summarized
			if (this.query.rowSummarizations.length > 0) {



			} else { // default to the first dimension
				var defaultSummarization = this.factTable.dimensions[0];
				this.columns.push({
					label: defaultSummarization.table.name,
					field: 'test'
				});
			}

			if (this.query.columnSummarizations.length > 0) {

			} else { // default to the second dimension

				var defaultSummarization = this.factTable.dimensions[1];
				this.columns.push({
					label: defaultSummarization.table.name,
					field: 'test'
				});	

			}
			

			//if (this.columns.summarizations[0])



			/*
				{
					label: '', 
					autoSave: true, 
					sortable: false, 
					renderHeaderCell:   this.hitch('renderPrimaryActionsHeader'),
					renderCell:         this.hitch('renderPrimaryActions')
				}
				,
				{ label: entityI18N.entitySummary_fieldGrid_nameColumn, field: 'name', sortable: false },
				{ label: entityI18N.entitySummary_fieldGrid_dataTypeColumn, field: 'dataType', sortable: false }
				*/
		},

		postCreate: function() {
			this.inherited(arguments);

			this.grid = new InternalGrid({
				columns: this.columns
			}, this.gridNode);

			//console.dir(this.factTable);
			//this.domHtml.set(this.domNode, 'PivotGrid...');

			this.listen(this.domNode, 'click', function() {
				if(this._configDialog == null) {
					this._configDialog = new ConfigurationDialog({
						contentParams: {
							factTable: this.factTable,
							queryDefinition: this.queryDefinition
						}
					});
				}
				this._configDialog.show();
			});
		}

	});
});