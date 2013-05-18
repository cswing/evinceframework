/*
 * Copyright 2013 Craig Swing.
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
	'dojo/dom-construct',
	'dojo/html',
	'evf/data/util',
	"evf/dataType/factory",
], function(array, domConstruct, html, dataUtil, dataTypeFactory) {
	
	/*=====
	FieldMetadata
	label
	dataType
	=====*/

	var exports = {
		// summary:
		//		This modules defines evf/form/layout, which provides functionality to create and layout forms
		//		based on field definition metadata.
	};

	exports.basic = function(parentNode, fields, columns){
		// summary:
		//		layout a form in a table.
		//
		// parentNode: DOM Node	
		//
		// fields: array
		//		the fields to layout
		//
		// columns: int
		//		(optional, defaults to 1) the number of columns to 
		//		display the fields.

		if(parentNode == null)
			throw 'parentNode is required.';

		var numCols = columns || 1,
			table = domConstruct.create('table', {'class': 'basicForm'}, parentNode),
			tbody = domConstruct.create('tbody', {}, table),
			tr,
			formWidgets = [];

		var rows = [];

		array.forEach(fields||[], function(fld, idx) {
			if (numCols % idx == 0) {
				tr = domConstruct.create('table', {'class': cls.join(' ')}, tbody);
				rows.push(tr);
			}

			var tdLabel = domConstruct.create('td', {'class':'label'}, tr);
			var lbl = domConstruct.create('lbl', {}, tdLabel);
			html.set(lbl, fld.label);

			var tdField = domConstruct.create('td', {'class':'label'}, tr);
			var w = dataTypeFactory.createWidget(fld.dataType, domConstruct.create('div', {}, tdField), fld);
			formWidgets.push(w);
		});

		// The total number of rows may not be known until after we are done.
		array.forEach(rows, function(tr, idx) {
			dataUtil.applyRowCss(tr, 'basicFormRow', idx, rows.length);
		});

		return {
			tableNode: 		table,
			formWidgets: 	formWidgets
		};
	};

	return exports;
});