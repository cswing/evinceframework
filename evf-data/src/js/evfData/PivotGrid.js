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
	//'dgrid/_StoreMixin',
	'dgrid/Grid', 
	'dgrid/extensions/DijitRegistry',
	'evf/_UtilMixin',
	'evf/dialog/util',
	'dojo/i18n!./nls/pivot'
], function(declare, Grid, DijitRegistry, UtilMixin, dialogUtil, i18n) {

	//var InternalGrid = declare([Grid], {});

	var defaultQuery = {
		summarizations: [],
		factCriterion: [],
		factSelections: []
	};

	return declare('evfData.PivotGrid', [Grid, DijitRegistry, UtilMixin], {

		factTable: null,

		query: null,

		postMixInProperties: function() {
			this.inherited(arguments);
			this.query = this.query || this.dojoLang.mixin({}, defaultQuery);

			this.columns = [];

			// summarization column
			var summaryDef = this.factTable.dimensions[0];
			if(!summaryDef) {
				dialogUtil.showErrorMessage(this.dojoLang.replace(i18n.error_noDimensions, [this.factTable.name]));
			}

			this.columns.push({
				label: summaryDef.table.name,
				field: 'test'
			})

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

			//var gridNode = this.domConstruct.create('div', {}, this.domNode);
			//this.grid = new InternalGrid();

			console.dir(this.factTable);
			//this.domHtml.set(this.domNode, 'PivotGrid...');
		}

	});
});