/*
 * Copyright 2012 Craig Swing.
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
	"dojo/_base/config",
	"dojo/_base/declare",
	"dojo/_base/kernel",
	"dojo/_base/lang",
	"dojo/dom-attr",
	"dojo/dom-class",
	"dojo/dom-construct",
	"dojo/dom-style",
	"dojo/on",
	"dojo/Evented",
	"dijit/_Widget"
], function(config, declare, kernel, lang, domAttr, domClass, domConstruct, domStyle, on, Evented, Widget){
	
	return declare("evf/ComplexWidget", [Widget, Evented], {
		// summary:
		//		A base widget class that provides default functionality.  
		//
		// description:
		//		This widget provides access to the following dom modules:
		//			- dojo/dom-attr
		//			- dojo/dom-class
		//			- dojo/dom-construct
		//			- dojo/dom-style
		//
		//		By using this widget, it is not required to include these dom modules in the require 
		//		statement.  The widget also adds the Evented mixin.  The result is the base for a 
		//		complex widget.
		
		kernel: kernel,
			// summary:
			// 		Provides convenient access to the Dojo kernal (dojo/_base/kernal).
		
		dojoConfig: config,
			// summary:
			// 		Provides convenient access to the Dojo configuration (dojo/_base/config). 
		
		lang: lang,
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
		
		domStyle: domStyle,
			// summary:
			// 		Provides convenient access to the dojo/dom-style module. 
		
		dojoOn: on,
			// summary:
			// 		Provides convenient access to the dojo/on module. 
		
		listen: function(target, type, listener, dontFix) {
			// summary:
			// 		A convenience method that registers an event listener in the 
			// 		context of the current widget.  When using this method, the 
			//		signature is that of dojo/on.  The listener will automatically
			//		be hitched to the widget and the widget will automatically take
			//		ownership of resulting handle.
			//
			//		NOTE: The preferred name for this method would have been on, but 
			//		_WidgetBase has an on function that assumes the domNode of the 
			//		widget as the target.
						
			var hndl = on(target, type, this.hitch(listener), dontFix);
			this.own(hndl);
			return hndl;
		}, 
		
		hitch: function(fn) {
			// summary:
			// 		A convenience method to hitch a function to this widget.
			
			return lang.hitch(this, fn);
		}
	});
});
