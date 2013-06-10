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
define([
	'dojo/_base/lang',
	'dijit/form/Button', 
	'./Dialog',
	'./DialogMessage',
	'dojo/i18n!./nls/dialog'
], function(lang, Button, Dialog, DialogMessage, i18n) {

	/*=====
	evf.dialog.__MessageObject = function(){
		//	title: String?
		//		Optional, the title of the dialog.  If not specified a default is used.
		//	type: String?
		//		Optional, when displaying a confiramtion dialog, the developer can override 
		//		the icon that is displayed to the user.  
		//	message: String
		//		the message to display to the user.
		this.title = title;
		this.type = type;
		this.message = message;
	}
	=====*/

	var exports = {
		//	summary:
		//		Utility functions to easily show confirmation, error, warning and 
		//		informational dialogs that are dijit/Dialogs and not browser dialogs 
		//		such as used with alert();

		messageTypes: {
			error: 		'error',
			warning: 	'warn',
			confirm: 	'confirm',
			info: 		'info'
		}
	};

	exports.showConfirmation = function(/*String || evf.exports.__MessageObject*/message, /*function?*/onConfirm, /*function?*/onAbort) {
		//	summary:
		// 		Display a confirmation messge in a dialog to the user with an OK and Cancel button.
		//	description:
		//		Display a confirmation message to the user, giving them the ability to confirm via
		//		an OK button or abort via a Cancel button.  The developer is notified of the user's 
		//		decision with the callback that is called.
		//		If the message is a string or the message object does not have a title defined, the 
		//		default title of 'Confirm' will be used.    
		//		If the message is a string or the message object does not have a type defined, the 
		//		default type will be confirm.
		//	message:
		//		the message to be displayed to the user.
		//	onConfirm:
		//		optional, the function to be called if the user confirms.
		//	onAbort:
		//		optional, the function to be called if the user aborts.
  
		var msgObj = exports._buildMessageObject(i18n.default_ConfirmTitle, message, exports.messageTypes.confirm);

		var dlg = new Dialog({
			title: msgObj.title,
			createContent: function(node) {
				return new DialogMessage({
					messageType: 	msgObj.type,
					message: 		msgObj.message
				}, node);
			},
			createActions: function(actionBar) {
				var btnOK = this.constructWidget(Button, { label: i18n.action_ok });
				actionBar.addChild(btnOK);
				this.listen(btnOK, 'click', function() {
					this._ok = true;
					this.hide();
				});

				var btnCancel = this.constructWidget(Button, { label: i18n.action_cancel });
				actionBar.addChild(btnCancel);
				this.listen(btnCancel, 'click', function() {
					this.hide();
				});

				if (onConfirm || onAbort) {
					this.own(this.aspect.before(this, 'hide', function() {
						if (this._ok) {
							if(onConfirm) onConfirm();
						} else {
							if(onAbort) onAbort();
						}
					}));
				}
			}
		});

		dlg.domClass.add(dlg.domNode, 'evfMessageDialog');
		dlg.show();
	}; // showConfirmation

	exports.showError = function(/*String || evf.exports.__MessageObject*/message, /*function?*/onAcknowledged) {
		//	summary:
		//		Display an error message in a dialog to the user with an OK button.
		//	description:
		//		Display an error message to the user. When the user acknowledges the 
		//		message by clicking the OK button, the developer is notified with the
		//		callback being called.
		//		If the message is a string or the message object does not have a title 
		//		defined, the default title of 'Error' will be used.    
		//		Calling this method implies that this is an error and the developer 
		//		cannot change the icon.
		//	message:
		//		the message to be displayed to the user.
		//	onAcknowledged:
		//		optional, the function to be called if the user acknowledges the error.
		var msg = exports._buildMessageObject(i18n.default_ErrorTitle, message, exports.messageTypes.error, true); 
		exports._showMessageWithOkOnly(msg, onAcknowledged);
	};

	exports.showWarning = function(/*String || evf.exports.__MessageObject*/message, /*function?*/onAcknowledged) {
		//	summary:
		//		Display a warning message in a dialog to the user with an OK button.
		//	description:
		//		Display a warning message to the user. When the user acknowledges the 
		//		message by clicking the OK button, the developer is notified with the
		//		callback being called.
		//		If the message is a string or the message object does not have a title 
		//		defined, the default title of 'Warning' will be used.    
		//		Calling this method implies that this is a warning and the developer 
		//		cannot change the icon.
		//	message:
		//		the message to be displayed to the user.
		//	onAcknowledged:
		//		optional, the function to be called if the user acknowledges the warning.
		var msg = exports._buildMessageObject(i18n.default_WarningTitle, message, exports.messageTypes.warning, true); 
		exports._showMessageWithOkOnly(msg, onAcknowledged);
	};

	exports.showInformation = function(/*String || evf.exports.__MessageObject*/message, /*function?*/onAcknowledged) {
		//	summary:
		//		Display an informational message in a dialog to the user with an OK button.
		//	description:
		//		Display an informational message to the user. When the user acknowledges the 
		//		message by clicking the OK button, the developer is notified with the
		//		callback being called.
		//		If the message is a string or the message object does not have a title 
		//		defined, the default title of 'Information' will be used.    
		//		Calling this method implies that this is an informational message and  
		//		the developer cannot change the icon.
		//	message:
		//		the message to be displayed to the user.
		//	onAcknowledged:
		//		optional, the function to be called if the user acknowledges the message.
		var msg = exports._buildMessageObject(i18n.default_InformationTitle, message, exports.messageTypes.info, true); 
		exports._showMessageWithOkOnly(msg, onAcknowledged);
	};

	exports._buildMessageObject = function(defaultTitle, message, messageType, alwaysOverwriteMessageType) {
		var msg = message;

		if (lang.isString(msg)) {
			msg = { message: message };
		}

		if (alwaysOverwriteMessageType || !msg.type) {
			msg.type = messageType;
		}

		if (!msg.title) {
			msg.title = defaultTitle; 
		}

		return msg;
	};

	exports._showMessageWithOkOnly = function(msgObj, callback) {
		exports.showContentWithOkOnly(msgObj.title, function(node) {
				return new DialogMessage({
					messageType: 	msgObj.type,
					message: 		msgObj.message
				}, node); 
			}, callback);
	};

	exports.showContentWithOkOnly = function(title, contentCreator, callback) {
		var dlg = new Dialog({
			title:  title,
			createContent: contentCreator,
			createActions: function(actionBar) {
				var btnOK = new Button({ label: i18n.action_ok });
				actionBar.addChild(btnOK);
				this.listen(btnOK, 'click', 'hide');
				if (callback) {
					this.own(this.aspect.before(dlg, 'hide', function() {
						callback();
					}));
				}
			}
		});
		dlg.domClass.add(dlg.domNode, 'evfMessageDialog');
		dlg.show();	
	}

	return exports;
});