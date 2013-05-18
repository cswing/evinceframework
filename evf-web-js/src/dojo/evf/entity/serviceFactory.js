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
    'dojo/_base/array'
], function(array) {

	var exports = {

		_hash: {},

		addField: 		'EVF_ENTITY_SERVICE_ADDFIELD',
		deleteField: 	'EVF_ENTITY_SERVICE_DELTEFIELD',
		updateField: 	'EVF_ENTITY_SERVICE_UPDATEFIELD'
	};

	var keys = [];
	for(var k in exports) {
		if(k != '_hash')
			keys.push(exports[k]);
	}
	exports.keys = keys;

	exports.registerServices = function(services) {
		for(var k in services) {
			if (array.some(exports.keys, function(key) { return key === k; })) {
				if(exports._hash[k]) {
					console.warn('An additional service is being registerd for ' + k + '. This original service will be discarded.');	
				}

				exports._hash[k] = services[k];
			} else {
				console.warn('Attempted to register an unknown service ' + k);
			}
		}

		array.forEach(exports.keys, function(key) {
			if(!exports._hash[key])
				console.warn('No service was registered for ' + key);
		});
	}

	exports.findService = function(key) {
		return exports._hash[key];
	}

	return exports;
});
