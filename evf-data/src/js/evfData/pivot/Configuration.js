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
	'dijit/layout/TabContainer',
	'evf/ComplexWidget',
	'evf/dataType/factory',
	'evf/dialog/SimpleFormDialog',
	'evf/dialog/util',
	'./DrillPathConfiguration',
	'./DrillPathConfigurationModel',
	'./FactConfiguration',
	'./util',
	'dojo/text!./templates/Configuration.html',
	'dojo/i18n!./nls/configuration'
], function(declare, Templated, TabContainer, ComplexWidget, dataFactory, SimpleFormDialog, dialogUtil, DrillPathConfiguration,
		ConfigurationModel, FactConfiguration, pivotUtil, template, i18n) {

	return declare('evfData.pivot.Configuration', [ComplexWidget, Templated], {

		templateString: template,

		factTable: null,

		queryDefinition: null,

		configModel: null,

		nameConstraints: {
			maxLength: 50
		},

		postMixInProperties: function() {
			this.inherited(arguments);

			this.i18n = i18n;
			this.queryDefinition = this.queryDefinition || pivotUtil.defaultQueryDefinition(this.factTable);

			this.configModel = new ConfigurationModel({
				factTable: this.factTable,
				queryDefinition: this.queryDefinition
			});
			
		},

		postCreate: function() {
			this.inherited(arguments);

			this.nameTextBox = dataFactory.createWidget('text', this.nameNode, {
				required: true,
				constraints: this.nameConstraints
			}, this.queryDefinition, 'name');

			this.domHtml.set(this.factTableNode, this.factTable.name);

			this.tabContainer = this.constructWidget(TabContainer, {
				tabPosition: 'left-h'
			}, this.tabContainerNode);

			this.factsTab = new FactConfiguration({
				title: i18n.tab_facts,
				configModel: this.configModel
			});
			this.tabContainer.addChild(this.factsTab);

			this.cols_PathsTab = new DrillPathConfiguration({
				title: i18n.tab_drillPaths_Cols,
				configModel: this.configModel,
				isRows: false
			});
			this.tabContainer.addChild(this.cols_PathsTab);

			this.row_PathsTab = new DrillPathConfiguration({
				title: i18n.tab_drillPaths_Rows,
				configModel: this.configModel,
				isRows: true
			});
			this.tabContainer.addChild(this.row_PathsTab);
		},

		startup: function() {
			this.inherited(arguments);
			this.defer(function() { this.tabContainer.resize(); });
		}

	});
});