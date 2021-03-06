/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0<div>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 define([
 	'dojo/_base/lang'
], function(lang) {

	var exports = {
		// summary:
		//


	};

	exports._defaultQueryDefinition = {
		
		_type: 					'evfData.queryDefinition',
		name: 					'',
		rowDrillPath: 			null,
		columnDrillPath: 		null,

		rowSummarizations: 		[],
		columnSummarizations: 	[],

		summarizations: [],
		factCriterion: [],
		factSelections: []
	};

	exports._defaultQuery = {

	};

	exports.defaultQueryDefinition = function(factTable) {
		return lang.mixin({
			factTable: 		factTable
		}, 
		exports._defaultQueryDefinition);
	};

	exports.defaultQuery = function(factTable) {
		var query = lang.mixin({}, exports._defaultQuery);
		query.definition = exports.defaultQueryDefinition(factTable);
		return query;
	};
	
	return exports;
});
