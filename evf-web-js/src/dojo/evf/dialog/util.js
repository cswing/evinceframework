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
  "dojo", "dijit/form/Button", "evf/dialog/Dialog",
  "dojo/i18n", "dojo/i18n!./nls/dialog"
], function(dojo, Button, Dialog) {

var util = dojo.getObject("evf.dialog.util", true);

/*=====
evf.dialog.util = {
  // summary: 
  //    utility methods to show confirmation, error, warning and informational dialogs
  //    that are dojo.Dialogs and not browser dialogs such as used with alert();      
}

evf.dialog.util.__MessageObject = function(){
  //  title: String?
  //    Optional, the title of the dialog.  If not specified a default is used.
  //  type: String?
  //    Optional, when displaying a confiramtion dialog, the developer can override 
  //    the icon that is displayed to the user.  
  //  message: String
  //    the message to display to the user.
  this.title = title;
  this.type = type;
  this.message = message;
}
=====*/

util.messageTypes = {
  error:    'error',
  warning:  'warn',
  confirm:  'confirm',
  info:     'info'
};

util.showConfirmation = function(/*String || evf.util.__MessageObject*/message, /*function?*/onConfirm, /*function?*/onAbort) {
  // summary:
  //    Display a confirmation messge in a dialog to the user with an OK and Cancel button.
  // description:
  //    Display a confirmation message to the user, giving them the ability to confirm via
  //    an OK button or abort via a Cancel button.  The developer is notified of the user's 
  //    decision with the callback that is called.
  //    If the message is a string or the message object does not have a title defined, the 
  //    default title of 'Confirm' will be used.    
  //    If the message is a string or the message object does not have a type defined, the 
  //    default type will be confirm.
  // message:
  //    the message to be displayed to the user.
  // onConfirm:
  //    optional, the function to be called if the user confirms.
  // onAbort:
  //    optional, the function to be called if the user aborts.
  
  var i18n = dojo.i18n.getLocalization("evf.dialog", "dialog");
  var msgObj = util._buildMessageObject(i18n.default_ConfirmTitle, message, util.messageTypes.confirm);
  
  var dlg = new Dialog({
    title: msgObj.title,
    createContent: function() {
      return new evf.dialog._DialogMessage({
        messageType:  msgObj.type,
        message:      msgObj.message
      });
    },
    createActions: function(actionBar) {
      var btnOK = new Button({ label: i18n.action_ok });
      actionBar.addChild(btnOK);
      this.connect(btnOK, 'onClick', function() {
        this._ok = true;
        this.hide();
      });
      
      var btnCancel = new Button({ label: i18n.action_cancel });
      actionBar.addChild(btnCancel);
      this.connect(btnCancel, 'onClick', function() {
        this.hide();
      });
      
      if (onConfirm || onAbort) {
        this.connect(this, 'hide', function() {
          if (this._ok) {
            if(onConfirm) onConfirm();
          } else {
            if(onAbort) onAbort();
          }
        });
      }
    }
  });
  dojo.addClass(dlg.domNode, 'evfMessageDialog');
  
  dlg.show();
};

util.showError = function(/*String || evf.util.__MessageObject*/message, /*function?*/onAcknowledged) {
  // summary:
  //    Display an error message in a dialog to the user with an OK button.
  // description:
  //    Display an error message to the user. When the user acknowledges the 
  //    message by clicking the OK button, the developer is notified with the
  //    callback being called.
  //    If the message is a string or the message object does not have a title 
  //    defined, the default title of 'Error' will be used.    
  //    Calling this method implies that this is an error and the developer 
  //    cannot change the icon.
  // message:
  //    the message to be displayed to the user.
  // onAcknowledged:
  //    optional, the function to be called if the user acknowledges the error.
  var i18n = dojo.i18n.getLocalization("evf.dialog", "dialog");
  var msg = util._buildMessageObject(i18n.default_ErrorTitle, message, util.messageTypes.error, true); 
  util._showMessageWithOkOnly(msg, onAcknowledged);
};

util.showWarning = function(/*String || evf.util.__MessageObject*/message, /*function?*/onAcknowledged) {
  // summary:
  //    Display a warning message in a dialog to the user with an OK button.
  // description:
  //    Display a warning message to the user. When the user acknowledges the 
  //    message by clicking the OK button, the developer is notified with the
  //    callback being called.
  //    If the message is a string or the message object does not have a title 
  //    defined, the default title of 'Warning' will be used.    
  //    Calling this method implies that this is a warning and the developer 
  //    cannot change the icon.
  // message:
  //    the message to be displayed to the user.
  // onAcknowledged:
  //    optional, the function to be called if the user acknowledges the warning.
  var i18n = dojo.i18n.getLocalization("evf.dialog", "dialog");
  var msg = util._buildMessageObject(i18n.default_WarningTitle, message, util.messageTypes.warning, true); 
  util._showMessageWithOkOnly(msg, onAcknowledged);
};

util.showInformation = function(/*String || evf.util.__MessageObject*/message, /*function?*/onAcknowledged) {
  // summary:
  //    Display an informational message in a dialog to the user with an OK button.
  // description:
  //    Display an informational message to the user. When the user acknowledges the 
  //    message by clicking the OK button, the developer is notified with the
  //    callback being called.
  //    If the message is a string or the message object does not have a title 
  //    defined, the default title of 'Information' will be used.    
  //    Calling this method implies that this is an informational message and  
  //    the developer cannot change the icon.
  // message:
  //    the message to be displayed to the user.
  // onAcknowledged:
  //    optional, the function to be called if the user acknowledges the message.
  var i18n = dojo.i18n.getLocalization("evf.dialog", "dialog");
  var msg = util._buildMessageObject(i18n.default_InformationTitle, message, util.messageTypes.info, true); 
  util._showMessageWithOkOnly(msg, onAcknowledged);
};

util._buildMessageObject = function(defaultTitle, message, messageType, alwaysOverwriteMessageType) {
  var msg = message;
  
  if (dojo.isString(msg)) {
    msg = { message: message };
  }
  
  if (alwaysOverwriteMessageType || !msg.type) {
    msg.type = messageType;
  }
  
  if (!msg.title) {
    msg.title = defaultTitle; 
  }
  
  return msg;
}

util._showMessageWithOkOnly = function(msgObj, callback) {
  
  var i18n = dojo.i18n.getLocalization("evf.dialog", "dialog");
  
  var dlg = new Dialog({
    title:  msgObj.title,
    createContent: function() {
      return new evf.dialog._DialogMessage({
        messageType:  msgObj.type,
        message:      msgObj.message
      }); 
    },
    createActions: function(actionBar) {
      var btnOK = new Button({ label: i18n.action_ok });
      actionBar.addChild(btnOK);
      this.connect(btnOK, 'onClick', 'hide');
      if (callback) {
        this.connect(dlg, 'hide', function() {
            callback();
        });
      }
    }
  });
  dojo.addClass(dlg.domNode, 'evfMessageDialog');
  
  dlg.show();
};

return util;
});
