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
define("evf/layout/Messages", [
  "dojo", "dijit", "dojo/dom-construct",
  "dijit/_Widget", "dijit/_TemplatedMixin", "dijit/_Container"
], function(dojo, dijit, domConstruct, Widget, Templated, Container) {

/*=====
evf.layout._Message = function(){
  //  type: String
  //    
  //  code: String
  //    
  //  message: String
  //    
  //  description: String
  //    
  this.type = type;
  this.code = code;
  this.message = message;
  this.description = description;
}
=====*/

dojo.declare("evf.layout._MessageDisplay", [Widget, Templated], {

  messageNode:  null,
  
  templateString: dojo.cache('evf.layout', 'templates/Message.html'),
  
  postMixInProperties: function() {
    this.inherited(arguments);
    this._startupWidgets = [];
  },
  
  postCreate: function() {
    this.inherited(arguments);
  },
  
  _setMessageAttr: function( /* evf.layout._Message */ msg) {
    dojo.addClass(this.domNode, msg.type.toString().toLowerCase());
    this.messageNode.innerHTML = msg.message;
  }
});

return dojo.declare("evf.layout.Messages", [Widget, Container], {
  
  hasError: null,
  
  postCreate: function() {
    this.inherited(arguments);
    dojo.addClass(this.domNode, 'messagesContainer');
    dojo.style(this.domNode, 'display', 'none');
  },
  
  _setMessagesAttr: function(msgs) {
    
    this.hasError = false;
    dojo.forEach(this.getChildren(), this.removeChild);
    
    var arr = msgs || [];
    dojo.forEach(arr, function (msg) {
      this.hasError = this.hasError || msg.type === 'error';
      this.addChild(new evf.layout._MessageDisplay({
        message: msg
      }));
    }, this);
    
    if(arr.length > 0) {
      dojo.style(this.domNode, 'display', '');
    }
  } 
});

});
