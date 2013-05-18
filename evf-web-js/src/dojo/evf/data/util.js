/*
 * Copyright 2012-3 Craig Swing
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
  'dojo/_base/lang',
  'dojo/dom-class'
], function(lang, domClass) {

	var util = {};

	/*=====
	evf.data.util = {
	  // summary: 
	  //    utility methods to help with data formatting      
	}
	
	=====*/

	util.formatFullName = function(nameAware) {
		// summary:
		//    Format a name as Last name, First name 
		// description:
		//    A utility function for formatting a full name. 
		// nameAware:
		//    A data object that has a lastName and/or firstName properties.  
		if (nameAware.firstName && nameAware.lastName) {
			return lang.replace("{lastName}, {firstName}", nameAware);
		}
		if (nameAware.firstName) {
			return nameAware.firstName;
		}
		return nameAware.lastName;
	};
	
	util.fullNameCellRenderer = function(td, data, rowIdx, colIdx, cellDef) {
		// summary:
		//    Renders a grid cell as a full name. 
		// description:
		//    A utility function for rendering a full name grid cell.
		//
		//
		td.innerHTML = util.formatFullName(data);
	};

	util.applyRowCss = function(node, baseCss, idx, totalRows) {
		// summary:
		//		A utility method that will apply first, last, odd, even css classes 
		//		based on the data.

		var cls = [baseCss];
		if(idx == 0)
			cls.push('first');
		
		cls.push(idx % 2 == 0 ? 'even' : 'odd');
		
		if(idx == totalRows - 1)
			cls.push('last');

		domClass.add(node, cls.join(' '));
	};
	
	return util;
});
