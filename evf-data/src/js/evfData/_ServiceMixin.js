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
 	'dojo/_base/config',
 	'dojo/_base/declare',
 	'evf/serviceRegistry'
 ], function(config, declare, serviceRegistry) {

	//var authenticationUrl = config.contextPath + '/security/authenticate';
	//serviceRegistry.registerServicePath(authenticationUrl);
	
	return declare('evfData._ServiceMixin', [/*assumes ComplexWidget*/], {
		// summary:
		// 		A mixin design to be included with the evf/layout/twib/_Layout to provide server communication 
		//		for data and charting that is part of the evf-data library.


	});
 });