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
define("evf/dialog/Dialog", [
	"dojo", "dijit", "dojo/dom-construct",
	"dijit/Dialog", "dijit/layout/BorderContainer",
	"dijit/_Widget", "dijit/_TemplatedMixin", "dijit/_Container", 
	"evf/layout/Messages"
], function(dojo, dijit, domConstruct, Dialog, BorderContainer, Widget, Templated, Container, Messages) {
	
	dojo.declare("evf.dialog._DialogMessage", [Widget, Templated], {
    messageNode:    null,
    iconNode:       null,    
    templateString: '<div class="dialogMessage"><div dojoAttachPoint="iconNode" class="dijitInline messageIcon ${messageType}"></div><div class="dijitInline messge"><span dojoAttachPoint="messageNode">${message}</span></div></div>'
  });

  var ActionBar = dojo.declare("evf.dialog._ActionBar", [Widget, Container], { 
		postCreate: function() {
			this.inherited(arguments);
			dojo.addClass(this.domNode, 'actionBar');
		}
	});
    
	return dojo.declare("evf.dialog.Dialog", [Dialog], {
	
		postCreate: function() {
			this.inherited(arguments);
			dojo.addClass(this.domNode, 'evfDialog');
			
			var div = domConstruct.create('div', {}, this.containerNode);
			this.createContainer(div);
		},
		
		createContainer: function(div) {
			this.container = new BorderContainer({
				gutters: false
			}, div);
			dojo.addClass(this.container.domNode, 'evfDialogContainer');
			/*
			this.messages = new Messages({
				region: 	'top'
			});
			this.container.addChild(this.messages);
			*/
			this.content = this.createContent();
			this.content.set('region', 'center');
			dojo.addClass(this.content.domNode, 'content');
			this.container.addChild(this.content);
			
			this.actionBar = new ActionBar({
				region: 	'bottom'
			});
			this.container.addChild(this.actionBar);
			this.createActions(this.actionBar);
			
			return this.container;
		},
		
		createContent: function() {
			throw 'createContent was not implemented.';
		},
		
		createActions: function(actionBar) {
			throw 'createActions was not implemented.';
		}
	});
});
    