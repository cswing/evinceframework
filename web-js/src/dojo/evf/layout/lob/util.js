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
  "dijit/_Widget", "dijit/_Container",
  "dijit/layout/AccordionContainer", "dijit/layout/ContentPane",
  "dijit/MenuBar", "dijit/MenuBarItem", "dijit/PopupMenuBarItem", 
  "dijit/DropDownMenu", "dijit/MenuItem", "dijit/PopupMenuItem"
], function(dojo, dijit, domConstruct, eLang, storeUtils, dialogUtils, 
    Widget, Container, AccordionContainer, ContentPane, 
    MenuBar, MenuBarItem, PopupMenuBarItem, DropDownMenu, MenuItem, PopupMenuItem) {

	var util = dojo.getObject('evf.layout.lob.util', true);
  
  
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