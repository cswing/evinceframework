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
	'dojo/i18n!../nls/layout'
], function(declare, Templated, ComplexWidget, MessageDisplay, SingleColumnLayout, i18n) {

	var Content = declare([ComplexWidget, Templated], {

		templateString: '<div><div data-dojo-attach-point="messagesNode"></div></div>',

		postCreate: function() {
			this.inherited(arguments);

			this.messagesDisplay = this.constructWidget(MessageDisplay, {
				showDescriptions: false
			}, this.messagesNode);

			if(this.viewModel) {
				this.messagesDisplay.set('messages', {
					messages: this.viewModel.query({ _type: 'evf.msg' }),
					clearPrevious: true
				});
			}
		}
	});

	return declare('evf.layout.twib._AccessDeniedPageMixin', [SingleColumnLayout], {
		
		postCreate: function() {
			this.inherited(arguments);
			
			this.domClass.add(this.domNode, 'accessDeniedPage');
			this.set('pageTitle', i18n.accessDeniedTitle);
			this.constructWidget(Content, { 
				viewModel: this.viewModel 
			}, this.contentNode);
		}

	});
});