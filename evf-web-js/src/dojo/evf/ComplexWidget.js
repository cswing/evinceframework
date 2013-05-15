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
	'dojo/_base/declare',
	'dojo/topic',
	'dojo/Evented',	
	'dijit/_Widget',
	'./_UtilMixin'
], function(array, declare, dojoTopic, Evented, Widget, UtilMixin){
	
	return declare('evf/ComplexWidget', [Widget, Evented, UtilMixin], {
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
		
        _startupWidgets: null,
			// summary:
			// 		[protected] An array of widgets that should have their startup method called when 
            //      this widget has its startup method called.
        
        postMixInProperties: function() {
            this.inherited(arguments);
            this.viewModel = this.dojoLang.getObject('viewModel');
        },

        startup: function(){
            if(this._started) return;
            this.inherited(arguments);
            
            if(this._startupWidgets) {
                array.forEach(this._startupWidgets, function(w){w.startup();});
            }
        },
        
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
			
			var hndl = this.dojoOn(target, type, this.hitch(listener), dontFix);
			this.own(hndl);
			return hndl;
		}, 
        
        publish: function(topic, event){
            // summary:
			// 		A convenience method to publish on a topic.
            
            dojoTopic.publish(topic, event);
        },
        
        subscribe: function(topic, listener) {
            // summary:
			// 		A convenience method to subscribe on a topic.  The listener will 
            //      automatically be hitched to the widget and the widget will 
            //      automatically take ownership of resulting handle.
			
            var hndl = dojoTopic.subscribe(topic, this.hitch(listener));
            this.own(hndl);
            return hndl;
        },
		
		ensureCtor: function(/*String|Widget ctor*/ctor) {
			// summary: 
			//		A utility method that will check if the constructor is a string.
			//		If so, it will use lang.getObject to get the actual constructor. 
			//		Otherwise, it assumes the value passed in is the actual constructor,

			if(this.dojoLang.isString(ctor)) {
                return this.dojoLang.getObject(ctor);
            }
            return ctor;
		},

        constructWidget: function(/*String|Widget ctor*/ctor, /*Object*/ props, /*DomNode*/ node) {
            // summary:
			// 		A convenience method to build a widget where the construtctor is either 
            //      the actual constructor or a string that represents the widget name.
            
            var wctor = this.ensureCtor(ctor);
            var w = new wctor(props, node);
            this.registerWidgetForStartup(w);

            return w;
        },
        
        registerWidgetForStartup: function(w) {
            // summary:
			// 		A method to register to have its startup method called when the startup of this
            //      widget is called.
            
            if(!this._startupWidgets) {
                this._startupWidgets = [];
            }
            this._startupWidgets.push(w);

            if(this._started && !w._started){
				w.startup();
			}
        }
	});
});
