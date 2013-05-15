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
	'dojo/_base/declare',
	'dijit/_Container',
	'dijit/Dialog',
	'evf/ComplexWidget',
	'./DialogMessage'
], function(declare, Container, Dialog, ComplexWidget, DialogMessage) {
	
	var ActionBar = dojo.declare([ComplexWidget, Container], { 
		postCreate: function() {
			this.inherited(arguments);
			this.domClass.add(this.domNode, 'evfDialogActionBar');
		}
	});
    
	return declare('evf.dialog.Dialog', [Dialog, ComplexWidget], {
	
		postCreate: function() {
			this.inherited(arguments);
			this.domClass.add(this.domNode, 'evfDialog');
			
			var div = this.domConstruct.create('div', {'class': 'evfDialogContainer'}, this.containerNode);
			this.content = this.createContent(this.domConstruct.create('div', {}, div));
			this.createActions(this.constructWidget(ActionBar, {}, 
				this.domConstruct.create('div', {}, div)));
		},
		
		createContent: function(node) {
			throw 'createContent was not implemented.';
		},
		
		createActions: function(actionBar) {
			throw 'createActions was not implemented.';
		}
	});
});
    