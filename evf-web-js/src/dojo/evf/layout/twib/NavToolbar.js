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
	//'evf/layout/navigation/menuUtil',
	'evf/ComplexWidget',
	'evf/layout/topics', 
	'dojo/text!./templates/NavToolbar.html'
], function(array, declare, Templated, DropDownButton, TooltipDialog, /*menuUtils,*/ ComplexWidget, layoutTopics, template){

	var messageTypeOrder = {
		'error': 0,
		'warn': 10,
		'info': 20
	};

	var MessageDisplay = declare([ComplexWidget, Templated], {

		templateString: '<div class="evfMessages"><div data-dojo-attach-point="containerNode" class="messageContainer"></div></div>',

		mostSignificantMessageType: null,

		postCreate: function() {
			this.inherited(arguments);
			this.domClass.add(this.domNode);
		},

		_setMessagesAttr: function(msgWrapper) {

			if(msgWrapper.clearPrevious === true) {
				this.mostSignificantMessageType = null;
				this.domConstruct.empty(this.containerNode);
			}

			var html = [],
				currentSignificantOrder = this.mostSignificantMessageType ? messageTypeOrder[this.mostSignificantMessageType] : 9999;

			array.forEach(msgWrapper.messages, function(msg) {
				html.push(this.dojoLang.replace(
					'<div class="message {msgType}" title="{description}"><div class="dijitInline icon"></div><div class="dijitInline code">[{code}]</div><div class="dijitInline text"><span>{message}</span></div></div>', 
					msg));

				// determine the most significant message type
				var order = messageTypeOrder[msg.msgType];
				if(order != null && order < currentSignificantOrder){
					this.mostSignificantMessageType = msg.msgType;
					currentSignificantOrder = order;
				}
			}, this);
			this.domHtml.set(this.containerNode, html.join(''));
		}

	});

	return declare('evf.layout.twib.NavToolbar', [ComplexWidget, Templated], {

		templateString: template,

		messageDisplay: null, 

		postCreate: function(){
			this.inherited(arguments);
			
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
			for(var k in messageTypeOrder) {
				this.domClass.remove(this.messageDropDown.domNode, k);
			}
			this.domClass.add(this.messageDropDown.domNode, this.messageDisplay.mostSignificantMessageType);
		}
	});
});