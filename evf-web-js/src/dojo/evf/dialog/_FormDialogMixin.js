/*
 * Copyright 2013 Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0<div>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 define([
	'dojo/_base/declare',
	'dijit/form/Button',
	'evf/ComplexWidget',
	'./Dialog',
	'dojo/i18n!../nls/common'
], function(declare, Button, ComplexWidget, Dialog, i18n) {

	return declare('evf.dialog._FormDialogMixin', [Dialog, ComplexWidget], {

		createActions: function(actionBar) {
			this.btnSave = this.constructWidget(Button, { label: i18n.action_save });
			actionBar.addChild(this.btnSave);
			this.listen(this.btnSave, 'click', '_onSubmit');

			this.btnCancel = this.constructWidget(Button, { label: i18n.action_cancel });
			actionBar.addChild(this.btnCancel);
			this.listen(this.btnCancel, 'click', function() { this.hide(); });
		},


		_onSubmit: function() {
			if(this.onSubmit()) {
				this.hide();
			}
		},

		onSubmit: function() {
			throw 'onSubmit not implemented.'
		}

	});

});