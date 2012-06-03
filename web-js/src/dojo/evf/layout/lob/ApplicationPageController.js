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
define("evf/layout/lob/ApplicationPageController", [
  "dojo", "dijit", "dojo/dom-construct",
  "evf/layout/lob/util", "evf/layout/navigation/AccordionNavigator"
], function(dojo, dijit, domConstruct, lobUtil, Navigator) {

return dojo.declare("evf.layout.lob.ApplicationPageController", [], {
  
  page: null,
  
  viewModel: null,
  
  constructor: function(params) {
    if(params){
      this.params = params;
      dojo.mixin(this, params);
    }
    
    if (!this.page) {
      throw 'page must be passed in.';
    }
    
    if (!this.viewModel) {
      throw 'viewModel must be passed in.';
    }
  },
  
  createLayout: function(page) {
    
    var _s = this;
    var fnCreate = function(createFn, region) {
      var w = dojo.hitch(_s, createFn)(page);
      if (w) {
        w.set('region', region);
        page.addChild(w);
      }
    };
    
    fnCreate(this.createSiteNavigation, 'leading');
    fnCreate(this.createContextNavigation, 'top');
    fnCreate(this.createMainContent, 'center');
    fnCreate(this.createFooter, 'bottom');
  },
  
  createSiteNavigation: function() {
    var qr = this.viewModel.query({ 
      _type:          'evf.siteNav'
    });
    
    if (qr.length == 0) {
      return null;
    }
    
    if (qr.length > 1) {
      throw 'multiple navigation mechanisms are not supported.';
      // TODO create a border container and create each navigator 
      //  inside the border container
    }
    
    var result = [];
    qr.forEach(function(nav) {
      // TODO support custom creator
      result.push(new Navigator({
    	  splitter:   	true,
          minSize:    	20,
          navigator:	nav
      }));  
    });
    return result[0];
  },
  
  createContextNavigation: function() {
    var qr = this.viewModel.query({ 
      _type:          'evf.contextNav'
    });
    
    if (qr.length == 0) {
      return null;
    }
    
    if (qr.length > 1) {
      throw 'multiple navigation mechanisms are not supported.';
    }
    
    var result = [];
    qr.forEach(function(nav) {
      // TODO support custom creator
      result.push(lobUtil.createMenuNavigation(this, nav));  
    });
    return result[0];
  },
  
  createMainContent: function() {
    throw 'createmainContent is undefined';
  },
  
  createFooter: function() {
    return null;
  }
  
});

});