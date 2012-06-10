/*
 * Copyright 2012 the original author or authors.
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
  "dojo"
], function(dojo) {

var util = dojo.getObject("evf.data.util", true);

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
		return dojo.replace("{lastName}, {firstName}", nameAware);
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

return util;
});
