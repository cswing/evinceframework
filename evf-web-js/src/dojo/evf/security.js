/*
 * Copyright 2013 Craig Swing
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
 	'dojo/_base/array',
	'dojo/_base/config',
 	'dojo/_base/declare',
 	'dojo/_base/lang'
], function(array, config, declare, lang) {

	var exports = {
		// summary:
		//		Security provides functionality to determine whether or not the user has a security right.
		//		Because this is javascript, this is not really security and more a mechanism to provider 
		//		a better user experience based on what you should have.  It is possible for the user to modify
		//		the underlying rights in the web browser.  Any server implementation should validate securtity
		//		before proceeding with a call.
		_rights: []
	};

	exports.registerRights = function(rights) {
		for(var k in rights) {
			if(k != '_rights') {
				exports._rights.push(rights[k]);
			}
		}
	};

	exports.hasSecurityRight = function(rightOrObject){
		// summary:
		//		A security method that returns true or false based on whether or not the 
		//		user has the right specified.  This method will look in the dojo configuration
		//		for a rights property that is a hash of right names and a boolean value 
		//		representing whether or not the user has the right.  If the right does not 
		//		exist in this hash, then the user is not considered to have the right.
		//
		// rightOrObject:
		//		Either a string or an object that has a key property that specifies the 
		//		security right.
		if(!rightOrObject) return false;

		var right = lang.isString(rightOrObject) ? rightOrObject : rightOrObject['key'];

		return config.rights && config.rights.indexOf(right) != -1;
	};
	
	return exports;
});
