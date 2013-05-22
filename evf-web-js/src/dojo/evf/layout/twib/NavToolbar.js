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
	'evf/layout/forms/AccountSummaryForm', 
	'evf/layout/forms/LoginForm',
	'evf/layout/rights',
	'evf/layout/topics', 
	'dojo/text!./templates/NavToolbar.html',
	'dojo/i18n!../forms/nls/account'
], function(array, declare, Templated, DropDownButton, TooltipDialog, /*menuUtils,*/ ComplexWidget, AccountSummaryForm, LoginForm, 
	rights, layoutTopics, template, i18n){

	return declare('evf.layout.twib.NavToolbar', [ComplexWidget, Templated], {

		templateString: template,

		viewModel: null,

		userNode: null,

		loginFormClass: LoginForm,

		accountFormClass: AccountSummaryForm,

		postCreate: function(){
			this.inherited(arguments);

			// TODO [EVALUATE]: The assumption is the account and login dialogs are mutually exclusive and which one 
			//  to display is decided once.  In order to support an SPA, the content should be converted to a stack 
			//  container with both dialogs as a child and helper methods to provide the ability to change which dialog 
			//  should be displayed.

			// Only add the right hand drop down if there is an authenticated user or support for authentication.

			var showAuthenticationDropdown = array.some([rights.authenticate, rights.register], this.hasSecurityRight, this);

			if (this.dojoConfig.user || showAuthenticationDropdown) {

			var dlgContentClass = this.dojoConfig.user ? this.accountFormClass : this.loginFormClass;
			var dlgContent = this.constructWidget(dlgContentClass, {
				viewModel: this.viewModel
			});

			this.userDialog = new TooltipDialog({
				content: dlgContent
			});
			this.domClass.add(this.userDialog.domNode, 'navBarTooltipDialog');

			this.accountDropDown = this.constructWidget(DropDownButton, {
				label:      this.dojoConfig.user ? this.dojoConfig.user.name : i18n.signIn,
				dropDown:   this.userDialog
			}, this.userNode);
			}
		},

		startup: function() {
			if(this._started) return;
			this.inherited(arguments);

			this.listen(this.logoNode, 'click', function() {
				this.publish(layoutTopics.goHome, {});
			});
		}
	});
});