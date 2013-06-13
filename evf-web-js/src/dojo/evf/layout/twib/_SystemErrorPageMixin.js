/*
 * Copyright 2013 the original author or authors.
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
	'dijit/_TemplatedMixin',
	'evf/ComplexWidget',
	'evf/MessageDisplay',
	'./SingleColumnLayout',
	'dojo/text!./templates/SystemErrorContent.html',
	'dojo/i18n!../nls/layout'
], function(declare, Templated, ComplexWidget, MessageDisplay, SingleColumnLayout, template, i18n) {

	var Content = declare([ComplexWidget, Templated], {

		templateString: template,

		postMixInProperties: function() {
			this.inherited(arguments);
			this.i18n = i18n;
		},

		postCreate: function() {
			this.inherited(arguments);

			this.messagesDisplay = this.constructWidget(MessageDisplay, {
				showDescriptions: true
			}, this.messagesNode);

			if(this.viewModel) {
				this.messagesDisplay.set('messages', {
					messages: this.viewModel.query({ _type: 'evf.msg' }),
					clearPrevious: true
				});
			}
		}
	});

	return declare('evf.layout.twib._SystemErrorPageMixin', [SingleColumnLayout], {
		
		postCreate: function() {
			this.inherited(arguments);
			
			this.domClass.add(this.domNode, 'systemErrorPage');
			this.set('pageTitle', i18n.sorry);
			this.constructWidget(Content, { 
				viewModel: this.viewModel 
			}, this.contentNode);
		}

	});
});