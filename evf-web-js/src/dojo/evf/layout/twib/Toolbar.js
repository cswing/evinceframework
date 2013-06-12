/*
 * Copyright 2013 Craig Swing.
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
	'dojo/_base/array',
	'dojo/_base/declare',
	'dijit/_TemplatedMixin',
	'dijit/form/DropDownButton', 
	'dijit/TooltipDialog',
	'evf/ComplexWidget',
	'evf/MessageDisplay',
	'evf/layout/navigation/menuUtil',
	'evf/layout/topics', 
	'dojo/text!./templates/Toolbar.html'
], function(array, declare, Templated, DropDownButton, TooltipDialog, ComplexWidget, MessageDisplay, menuUtils, layoutTopics, template){

	return declare('evf.layout.twib.Toolbar', [ComplexWidget, Templated], {

		templateString: template,

		viewModel: null,

		messageDisplay: null, 

		postCreate: function(){
			this.inherited(arguments);
			
			// menu
			this._buildMenu();

			// messaging
			this.messageDisplay = new MessageDisplay({});
			this.messageDialog = new TooltipDialog({
				content: this.messageDisplay
			});
			this.domClass.add(this.messageDialog.domNode, 'evfToolbarDialog evfMessagesTooltipDialog');

			this.messageDropDown = this.constructWidget(DropDownButton, {
				iconClass: 	'messagesIcon',
				dropDown:   this.messageDialog
			}, this.messageNode);
			this.domClass.add(this.messageDropDown.domNode, 'messagesButton');
			this.domStyle.set(this.messageDropDown.domNode, 'display', 'none');
		},

		_buildMenu: function() {
			var qr = this.viewModel.query({_type: 'evf.siteNav'});

			if (qr.length == 0) return;
			if (qr.length > 1)
				throw 'multiple navigation mechanisms are not supported.';

			menuUtils.buildNavigation(qr[0], this.menuNode, 'toolbarMenuPopup');
		},

		startup: function() {
			if(this._started) return;
			this.inherited(arguments);

			this.listen(this.logoNode, 'click', function() {
				this.publish(layoutTopics.goHome, {});
			});

			this.subscribe(layoutTopics.messageNotification, '_onMessages');
		},

		_onMessages: function(data) {
			this.messageDisplay.set('messages', data);

			// only show if there are messages
			this.domStyle.set(this.messageDropDown.domNode, 'display', 
				this.messageDisplay.mostSignificantMessageType ? '' : 'none');

			// show the icon of the most significant message
			for(var k in this.messageDisplay.messageTypeOrder) {
				this.domClass.remove(this.messageDropDown.domNode, k);
			}
			this.domClass.add(this.messageDropDown.domNode, this.messageDisplay.mostSignificantMessageType);
		}
	});
});