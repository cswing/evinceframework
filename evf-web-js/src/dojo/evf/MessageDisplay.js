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
	'dojo/query',
	'dojo/NodeList-dom',
	'dijit/_TemplatedMixin',
	'evf/ComplexWidget'
], function(array, declare, query, domNodeList, Templated, ComplexWidget){

	return declare('evf.MessageDisplay', [ComplexWidget, Templated], {

		templateString: '<div class="evfMessages"><table class="messageContainer"><tbody data-dojo-attach-point="containerNode"></tbody></table></div>',

		messageTypeOrder: {
			'error': 0,
			'warn': 10,
			'info': 20
		},

		showMessageCodes: null,

		showDescriptions: null,

		mostSignificantMessageType: null,

		postMixInProperties: function() {
			this.inherited(arguments);

			if (this.showMessageCodes == null)
				this.showMessageCodes = true;
		},

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
				currentSignificantOrder = this.mostSignificantMessageType ? this.messageTypeOrder[this.mostSignificantMessageType] : 9999;

			array.forEach(msgWrapper.messages, function(msg) {
				html.push(this.dojoLang.replace(
					'<tr class="message {msgType}" title="{description}"><td class="iconCell"><div class="icon"></div></td><td class="code" data-dojo-attach-point="codeNode">[{code}]</td><td class="text"><span>{message}</span></td></tr>', 
					msg));

				if(this.showDescriptions) {
					html.push(this.dojoLang.replace(
					'<tr class="messageDescription {msgType}"><td colspan="3">{description}</td></tr>', 
					msg));
				}

				// determine the most significant message type
				var order = this.messageTypeOrder[msg.msgType];
				if(order != null && order < currentSignificantOrder){
					this.mostSignificantMessageType = msg.msgType;
					currentSignificantOrder = order;
				}

				msg._devAck = true;

			}, this);
			this.domHtml.set(this.containerNode, html.join(''));

			if(!this.showMessageCodes) {
				query('.code', this.containerNode).style('display', 'none');
			}
		}

	});
});