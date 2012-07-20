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
  "dojo", "evf/store/ViewModel"
], function(dojo, ViewModel) {

var query = dojo.getObject("evf.data.queryService", true);

/*=====
evf.data.queryService = {
  // summary: 
  //    methods to communicate with json services that implement the evince Query API.      
}

=====*/

query.callService = function(serviceUrl, parameters, onSuccess, onError) {
	// summary:
	//    Call the json query service with provided parameters 
	// description:
	//     
	// serviceUrl:
	//    
	// parameters:
	//
	
	/*
	order:[object Object]
order:[object Object]
page:5
pageSize:25
	*/
	
	var postForm = {
		page:		parameters.page,
		pageSize:	parameters.pageSize
	};
	
	if (parameters.order) {
		dojo.forEach(parameters.order, function(o, idx) {
			var key = 'orders[' + idx + ']';
			postForm[key+'.ascending'] = o.ascending;
			postForm[key+'.sortField'] = o.sortField;
		});
	}
	
	var xhrArgs = {
		url: 		serviceUrl,
		handleAs:	'json',
		postData:   dojo.objectToQuery(postForm)
	};
	dojo.xhrPost(xhrArgs)
		.then(function(data) {
			var store = new ViewModel({data: data}); 
			onSuccess(store);
		}, onError);
};

return query;
});
