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
define("evf/layout/lob/util", [
  "dojo", "dijit", "dojo/dom-construct",
  "evf/_lang", "evf/store/util", "evf/dialog/util",
  "dijit/layout/AccordionContainer", "dijit/layout/ContentPane",
  "dijit/MenuBar", "dijit/MenuBarItem", "dijit/PopupMenuBarItem", 
  "dijit/DropDownMenu", "dijit/MenuItem", "dijit/PopupMenuItem"
], function(dojo, dijit, domConstruct, eLang, storeUtils, dialogUtils, 
    AccordionContainer, ContentPane, 
    MenuBar, MenuBarItem, PopupMenuBarItem, DropDownMenu, MenuItem, PopupMenuItem) {

  var util = dojo.getObject('evf.layout.lob.util', true);
  
  util.createAccordionNavigation = function(controller, nav) {
    
    var processItems = function(accordion, parentItem, parentNode) {
      var items = storeUtils.sort(parentItem.items.clone(), 'order');
      dojo.forEach(items, function(navItem) {
        
        // three options: children, url, or command
        if (navItem.items) {
          
          var span = domConstruct.create('span', { 'class': 'itemGroupTitle' }, parentNode);
          span.innerHTML = navItem.title;
          
          var ul = domConstruct.create('ul', { 'class': 'itemGroup' }, parentNode);
          processItems(accordion, navItem, ul);
          
          return;
        }
        
        var li = domConstruct.create('li', { 'class': 'item' }, parentNode);
        var a = domConstruct.create('a', {}, li);
        a.innerHTML = navItem.title; 
         
        if (navItem.url) {
        	var url = navItem.url;
        	if (!url.startsWith("http")) {
        		var ctxPath = dojo.getObject('contextPath');
        		if (ctxPath && ctxPath != '') {
        			url = dojo.replace("{0}/{1}", [ctxPath, url]).replace('//', '/');
        		}
        	}
        	dojo.attr(a, 'href', url);
        	
        } else {
            accordion.connect(a, 'onclick', function() {
	          dialogUtils.showConfirmation('Are you sure?');
	        });
        }
      });
    };
    
    var accordion = new dijit.layout.AccordionContainer({
      splitter:   true,
      minSize:    20
    });
    dojo.addClass(accordion.domNode, 'navigation');
    
    var items = nav.items || [];
    items = storeUtils.sort(items.clone(), 'order');
    
    dojo.forEach(items, function(navItem) {
      // TODO support custom pane
      var pane = new dijit.layout.ContentPane({
        title:      navItem.title
      });
      accordion.addChild(pane);
      
      var ul = domConstruct.create('ul', { 'class': 'itemGroup' }, pane.domNode);
      processItems(accordion, navItem, ul);
    });
    
    return accordion;
  };
  
  util.createMenuNavigation = function(controller, nav) {
    var mnuBar = MenuBar({});
    dojo.addClass(mnuBar.domNode, 'navigation');
    
    var createMenuItemGroup = function(navItem, widget) {
      
      var subMenu = new DropDownMenu({});
        
      dojo.forEach(navItem.items, function(navItem) {
        if (navItem.items) {
          subMenu.addChild(createMenuItemGroup(navItem, PopupMenuItem));
        } else {
          subMenu.addChild(createMenuItem(navItem, MenuItem));  
        }  
      });
      
      return new widget({
          label:  navItem.title,
          popup:  subMenu
      });
    };
    
    var createMenuItem = function(itm, widget) {
      var menuItem = new widget({
        label: itm.title
      });
       
      mnuBar.connect(menuItem, 'onClick', function() {
        dialogUtils.showConfirmation('Are you sure?');
      });
      
     return menuItem;
    };
    
    var items = nav.items || [];
    items = storeUtils.sort(items.clone(), 'order');
    dojo.forEach(items, function(navItem) {
      
      if (navItem.items) {
        
        mnuBar.addChild(
          createMenuItemGroup(navItem, PopupMenuBarItem)
        );
        
      } else {
        
        mnuBar.addChild(
          createMenuItem(navItem, MenuBarItem)
        );
      }      
    });
        
    return mnuBar;
  };
  
  return util;
});