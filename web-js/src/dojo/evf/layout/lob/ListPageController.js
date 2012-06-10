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
define("evf/layout/lob/ListPageController", [
  "dojo", "dijit", "dojo/dom-construct",
  "dijit/_Widget", "dijit/DropDownMenu", "dijit/form/DropDownButton",
  "evf/layout/lob/ApplicationPageController", "evf/data/Pager", "dijit/layout/ContentPane",
  "evf/data/Grid"
], function(dojo, dijit, domConstruct, Widget, DropDownMenu, DropDownButton,
    ApplicationPageController, Pager, ContentPane, Grid) {

return dojo.declare("evf.layout.lob.ListPageController", [ApplicationPageController], {
  
  searchPane: 	null,
  
  pager:  		null,
  
  metadata: 	null,
  /// com.evinceframework.data.QueryResult
  
  createMainContent: function(page) {
    
    this.searchPane = new ContentPane({ content: 'List Search', region: 'top' });
    dojo.style(this.searchPane.domNode, 'height', '125px'); 
    page.addChild(this.searchPane);
        
    //this.topPager = new Pager({ metadata: this.metadata, region: 'top' });
    //page.addChild(this.topPager);
    
    var qr = this.getStore().query({ 
    	_type:          'evf.query.result'
    });
    
    if (qr.length > 1) {
    	throw 'multiple query results are not supported.';
    }
    
    if (qr.length == 0) {
    	this.metadata = {
    		parameters:		{},
    		page: 			1,
    		totalPages: 	1,
    		totalItems:		0,
    		items: 			[]
    	};
    } else {
    	this.metadata = qr[0];
    }
    
    this.grid = new Grid({
      structure:    this.getStructure(),
      items:		this.metadata.items || []
    }); 
    
    // wrap in a pane widget, to get proper resize
    var pane = new ContentPane({ content: this.grid });
    dojo.addClass(pane.domNode, 'dataGridPane');
    return pane;
  },
  
  createFooter: function(page) {
    //var res = this.inherited(arguments);
    
    this.bottomPager = new Pager({ metadata: this.metadata, region: 'bottom' });
    page.addChild(this.bottomPager);
    
    return this.bottomPager;
  },
  
  getStructure: function() {
    throw 'structure has not been provided';  
  },
  
  getStore: function() {
    return this.viewModel;
  },
  
  createMenuCellDef: function() {
    return { 
      controller: this,
      field: "__actions",
      width: 20, 
      renderHeader: function(th, cellDef, colIdx) {
        th.innerHTML = "&nbsp;";
        dojo.addClass(th, 'menuCellHeader');
      },
      renderCell:   function(td, data, rowIdx, colIdx, cellDef) {
        
        dojo.removeClass(td, 'columnData');
        dojo.addClass(td, 'menuCell');
        
        var subMenu = new DropDownMenu({});
        this.controller.buildActions(data, subMenu);
        
        var button = new DropDownButton({
             dropDown:    subMenu,
             showLabel:   false,
             iconClass:   'gridMenu'
        }, domConstruct.create('div', {}, td));
        dojo.addClass(button.domNode, 'gridRowMenu');
        dojo.query('.dijitArrowButtonInner, .dijitArrowButtonChar', 
          button.domNode).style('display', 'none');
      }
    };
  }
      
});

});