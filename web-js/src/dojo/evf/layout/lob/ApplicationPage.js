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
define("evf/layout/lob/ApplicationPage", [
  "dojo", "dijit", "dojo/dom-construct",
  "dijit/_Widget", "dijit/_TemplatedMixin", 
  "dijit/layout/BorderContainer", "dijit/layout/_LayoutWidget"
], function(dojo, dijit, domConstruct, Widget, Templated, Border, Layout) {

// "evf.layout.lob.ApplicationPageController"
// createLayout

return dojo.declare("evf.layout.lob.ApplicationPage", [Border], {

  viewModel:      null,
  
  controller:     '',
    
  liveSplitters:  false,
  
  design:         'sidebar',
  
  postMixInProperties: function() {
    this.inherited(arguments);
    if (!this.controller) {
      throw 'controller was not defined';
    }
    var Controller = dojo.getObject(this.controller); 
    this.controller = new Controller({
      page:       this,
      viewModel:  this.viewModel
    });
  },
  
  postCreate: function() {
    this.inherited(arguments);
    dojo.addClass(this.domNode, 'lobAppPage');
    this.controller.createLayout(this);
  }
  
});

});