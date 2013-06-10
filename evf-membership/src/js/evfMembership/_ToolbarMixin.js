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
	'dojo/_base/array',
	'dojo/_base/declare',
	'dijit/form/DropDownButton', 
	'dijit/TooltipDialog',
	'./rights',
	'./forms/AccountSummaryForm',
	'./forms/LoginForm',
	'dojo/i18n!./nls/membership'
], function(array, declare, DropDownButton, TooltipDialog, rights, AccountSummaryForm, LoginForm, i18n){

	return declare('evfMembership._ToolbarMixin', [/*assumes ComplexWidget*/], {
		// summary:
		// 		A mixin design to be included with the evf/layout/twib/NavToolbar to provide authentication 
		// 		and profile support that is part of the evf-membership library.
		//
		//		THis mixin assumes there is a userNode that is to be used to instantiate the dropdown widget.

		userNode: null,

		postCreate: function(){
			this.inherited(arguments);

			// TODO [EVALUATE]: The assumption is the account and login dialogs are mutually exclusive and which one 
			//  to display is decided once.  In order to support an SPA, the content should be converted to a stack 
			//  container with both dialogs as a child and helper methods to provide the ability to change which dialog 
			//  should be displayed.

			// Only add the right hand drop down if there is an authenticated user or support for authentication.

			var showAuthenticationDropdown = array.some([rights.authenticate, rights.register], this.hasSecurityRight, this);
			var showUserProfileDropdown = array.some([rights.authenticatedUser], this.hasSecurityRight, this);

			if(!showAuthenticationDropdown && !showUserProfileDropdown)
				return;

			var dlgContentClass = showUserProfileDropdown ? AccountSummaryForm : LoginForm;
			var dlgContent = this.constructWidget(dlgContentClass, {
				viewModel: this.viewModel
			});

			this.userDialog = new TooltipDialog({
				content: dlgContent
			});
			this.domClass.add(this.userDialog.domNode, 'evfToolbarDialog evfMembershipTooltipDialog');

			this.accountDropDown = this.constructWidget(DropDownButton, {
				label:      this.dojoConfig.user ? this.dojoConfig.user.name : i18n.signIn,
				dropDown:   this.userDialog
			}, this.userNode);
		}
	});
});