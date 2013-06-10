/*
 * Copyright 2012-2013 the original author or authors.
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
	'evf/serviceRegistry' // load the stuff necessary for remote services
], function(declare, serviceRegistry) {

	return declare('evf.layout._ServiceMixin', [], {

		acknowledgeMessage: function(msg) {
			// summary:
			//		A convience method to acknoledge a message as being handled by the service 
			//		calling code.  Messages that have not been acknowledged will be displayed 
			//		using generic messaging display.			

			return serviceRegistry.acknowledgeMessage(msg);
		}
	});
});