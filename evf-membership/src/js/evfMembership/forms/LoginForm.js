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
	'dojo/keys', 
	'dijit/_TemplatedMixin',
	'dijit/form/ValidationTextBox',
	'dijit/form/Button',
	'dijit/form/CheckBox',
	'evf/ComplexWidget',
	'evf/MessageDisplay',
	'../roles',
	'../topics', 
	'dojo/text!./templates/LoginForm.html',
	'dojo/i18n!../nls/membership'
], function(declare, keys, Templated, ValidationTextBox, Button, CheckBox, ComplexWidget, MessageDisplay, roles, topics, template, i18n){

	return declare('evfMembership.forms.LoginForm', [ComplexWidget, Templated], {

		templateString: template,

		loginTextBox: null,

		passwordTextBox: null,

		rememberMeCheckBox: null,

		postMixInProperties: function() {
			this.inherited(arguments);
			this.i18n = i18n;
		},

		postCreate: function(){
			this.inherited(arguments);

			this.messageDisplay = 
				this.constructWidget(MessageDisplay, {
					showMessageCodes: false
				}, this.messagesNode);

			this.loginTextBox = this.constructWidget(ValidationTextBox, {
				name: 'loginId',
				value: '',
				placeHolder: i18n.loginId
			}, this.loginIdNode);

			this.passwordTextBox = this.constructWidget(ValidationTextBox, {
				name: 'password',
				type: 'password', 
				value: '',
				placeHolder: i18n.password
			}, this.passwordNode);

			if(this.hasSecurityRole(roles.rememberMe)) {
				this.rememberMeCheckBox = this.constructWidget(CheckBox, {
					name: 'rememberMe'
				}, this.rememberMeNode);

			} else {
				this.domStyle.set(this.rememberMeNode, 'display', 'none');
			}

			this.submitButton = this.constructWidget(Button, {
				label: i18n.loginAction
			}, this.submitNode);
			this.listen(this.submitButton, 'click', '_authenticate');

			// Forgot Password link
			if(this.hasSecurityRole(roles.resetPassword)) {
				this.forgotButton = this.constructWidget(Button, {
					label: i18n.forgotAction
				}, this.forgotPasswordNode);
			} else {
				this.domStyle.set(this.forgotPasswordNode, 'display', 'none');
			}

			// Register link
			if(this.hasSecurityRole(roles.register)) {
				this.forgotButton = this.constructWidget(Button, {
					label: i18n.registerAction
				}, this.registerNode);
			} else {
				this.domStyle.set(this.registerContainerNode, 'display', 'none');
			}
		},

		_authenticate: function() {
			// TODO validate

			this.publish(topics.requestAuthentication, {
				loginId: this.loginTextBox.get('value'),
				password: this.passwordTextBox.get('value'),
				callback: this.hitch(this._onAuthentication)
			});
		},

		_onAuthentication: function(vm) {
			// Assumption that on successful authentication, the service will perform
			// the redirect.  This function is only responsible for showing security 
			// related error messages.

			this.messageDisplay.set('messages', {
				messages: vm.query({ _type: 'evf.msg', code: 'HTTP-401' }),
				clearPrevious: true
			});
		},

		_onKeyUp: function(keyEvent) {
			if(keyEvent.keyCode != keys.ENTER)
				return;

			this._authenticate();
		}
	});
});