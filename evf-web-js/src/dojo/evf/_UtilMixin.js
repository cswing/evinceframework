/*
 * Copyright 2013 Craig Swing.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
define([
    'dojo/_base/array',
	'dojo/_base/config',
	'dojo/_base/declare',
	'dojo/_base/kernel',
	'dojo/_base/lang',
    'dojo/aspect',
	'dojo/dom-attr',
	'dojo/dom-class',
	'dojo/dom-construct',
	'dojo/dom-geometry',
    'dojo/html',
	'dojo/dom-style',
	'dojo/on',
	'evf/security'
], function(array, config, declare, kernel, lang, aspect, domAttr, domClass, domConstruct, domGeom, domHtml, domStyle, on, security){

	return declare('evf._UtilMixin', [], {
		// summary:
		//		A base mixin that provides access to default functionality.  
		//
		// description:
		//		This widget provides access to the following dom modules:
		//			- dojo/dom-attr
		//			- dojo/dom-class
		//			- dojo/dom-construct
		//			- dojo/dom-style
		        
		kernel: kernel,
			// summary:
			// 		Provides convenient access to the Dojo kernal (dojo/_base/kernal).
		
		dojoConfig: config,
			// summary:
			// 		Provides convenient access to the Dojo configuration (dojo/_base/config). 
        
        aspect: aspect,
        	// summary:
			// 		Provides convenient access to the dojo/aspect module. 
	    
		dojoLang: lang,
			// summary:
			// 		Provides convenient access to the dojo/_base/lang module. 
		
		domAttr: domAttr, 
			// summary:
			// 		Provides convenient access to the dojo/dom-attr module. 
		
		domClass: domClass, 
			// summary:
			// 		Provides convenient access to the dojo/dom-class module. 
		
		domConstruct: domConstruct, 
			// summary:
			// 		Provides convenient access to the dojo/dom-construct module. 
		
		domGeom: domGeom,
			// summary:
			// 		Provides convenient access to the dojo/dom-geometry module. 

        domHtml: domHtml,
			// summary:
			// 		Provides convenient access to the dojo/dom-html module. 
        
		domStyle: domStyle,
			// summary:
			// 		Provides convenient access to the dojo/dom-style module. 
		
		dojoOn: on,
			// summary:
			// 		Provides convenient access to the dojo/on module. 

		hitch: function(fn) {
			// summary:
			// 		A convenience method to hitch a function to this widget.
			
			return this.dojoLang.hitch(this, fn);
		},

		hasSecurityRole: function(roleOrObject){
			// summary:
			//		A security method that calls into the security module to determine 
			//		if the user has a particular security role.
			//
			// roleOrObject:
			//		Either a string or an object that has a key property that specifies the 
			//		security role.
			return security.hasSecurityRole(roleOrObject);
		}
	});
});