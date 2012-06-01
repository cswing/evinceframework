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
      
      // PopupMenuItem
      
      /*
      
      var pSubMenu = new dijit.DropDownMenu({});
      pSubMenu.addChild(new dijit.MenuItem({
          label:"File item #1"
      }));
      pSubMenu.addChild(new dijit.MenuItem({
          label:"File item #2"
      }));
      pMenuBar.addChild(new dijit.PopupMenuBarItem({
          label:"File",
          popup:pSubMenu
      }));

      var pSubMenu2 = new dijit.DropDownMenu({});
      pSubMenu2.addChild(new dijit.MenuItem({
          label:"Edit item #1"
      }));
      pSubMenu2.addChild(new dijit.MenuItem({
          label:"Edit item #2"
      }));
      pMenuBar.addChild(new dijit.PopupMenuBarItem({
          label:"Edit",
          popup:pSubMenu2
      }));

       pMenuBar.placeAt("wrapper");
       pMenuBar.startup();
      */
      
    });
    
    
    return mnuBar;
/*
   <div id="header" data-dojo-type="dijit.MenuBar" data-dojo-props="region:'top'">
      <div data-dojo-type="dijit.PopupMenuBarItem" id="file">
        <span>File</span>
        <div data-dojo-type="dijit.Menu" id="fileMenu">
          <div data-dojo-type="dijit.MenuItem" id="globals" data-dojo-props="onClick: logStrayGlobals">Log globals</div>
          <div data-dojo-type="dijit.MenuItem" id="widgets" data-dojo-props="onClick: logWidgets">Log widgets</div>
          <div data-dojo-type="dijit.MenuItem" id="destroy" data-dojo-props="iconClass:'dijitIconDelete', onClick:tearDown">Destroy All</div>
        </div>
      </div>
      <div data-dojo-type="dijit.PopupMenuBarItem" id="edit">
        <span>Edit</span>
        <div data-dojo-type="dijit.Menu" id="editMenu">
          <div data-dojo-type="dijit.MenuItem" id="cut" data-dojo-props="
            iconClass:'dijitIconCut',
            onClick:function(){ console.log('not actually cutting anything, just a test!') }
          ">Cut</div>
          <div data-dojo-type="dijit.MenuItem" id="copy" data-dojo-props="
            iconClass:'dijitIconCopy',
            onClick: function(){ console.log('not actually copying anything, just a test!') }
          ">Copy</div>
          <div data-dojo-type="dijit.MenuItem" id="paste" data-dojo-props="iconClass:'dijitIconPaste',
            onClick: function(){ console.log('not actually pasting anything, just a test!') }">Paste</div>
          <div data-dojo-type="dijit.MenuSeparator" id="separator"></div>
          <div data-dojo-type="dijit.MenuItem" id="undo" data-dojo-props="iconClass:'dijitIconUndo'">Undo</div>
        </div>
      </div>
      <div data-dojo-type="dijit.PopupMenuBarItem" id="view">
        <span>View</span>
        <div data-dojo-type="dijit.Menu" id="viewMenu">
          <div data-dojo-type="dijit.MenuItem">Normal</div>
          <div data-dojo-type="dijit.MenuItem">Outline</div>
          <div data-dojo-type="dijit.PopupMenuItem">
            <span>Zoom</span>
            <div data-dojo-type="dijit.Menu" id="zoomMenu">
              <div data-dojo-type="dijit.MenuItem">50%</div>
              <div data-dojo-type="dijit.MenuItem">75%</div>
              <div data-dojo-type="dijit.MenuItem">100%</div>
              <div data-dojo-type="dijit.MenuItem">150%</div>
              <div data-dojo-type="dijit.MenuItem">200%</div>
            </div>
          </div>
        </div>
      </div>
      <div data-dojo-type="dijit.PopupMenuBarItem" id="themes">
        <span>Themes</span>
        <div data-dojo-type="dijit.Menu" id="themeMenu"></div>
      </div>
      <div data-dojo-type="dijit.PopupMenuBarItem" id="dialogs">
        <span>Dialogs</span>
        <div data-dojo-type="dijit.Menu" id="dialogMenu">
          <div data-dojo-type="dijit.MenuItem" data-dojo-props="onClick: showDialog">slow loading</div>
          <div data-dojo-type="dijit.MenuItem" data-dojo-props="onClick: showDialogAb">action bar</div>
        </div>
      </div>
      <div data-dojo-type="dijit.PopupMenuBarItem" id="inputPadding">
        <span>TextBox Padding</span>
        <div data-dojo-type="dijit.Menu" id="inputPaddingMenu">
          <div data-dojo-type="dijit.CheckedMenuItem" data-dojo-props="onClick:setTextBoxPadding, checked:true">theme default</div>
          <div data-dojo-type="dijit.CheckedMenuItem" data-dojo-props="onClick:setTextBoxPadding">0px</div>
          <div data-dojo-type="dijit.CheckedMenuItem" data-dojo-props="onClick:setTextBoxPadding">1px</div>
          <div data-dojo-type="dijit.CheckedMenuItem" data-dojo-props="onClick:setTextBoxPadding">2px</div>
          <div data-dojo-type="dijit.CheckedMenuItem" data-dojo-props="onClick:setTextBoxPadding">3px</div>
          <div data-dojo-type="dijit.CheckedMenuItem" data-dojo-props="onClick:setTextBoxPadding">4px</div>
          <div data-dojo-type="dijit.CheckedMenuItem" data-dojo-props="onClick:setTextBoxPadding">5px</div>
        </div>
      </div>
      <div data-dojo-type="dijit.PopupMenuBarItem" id="help">
        <span>Help</span>
        <div data-dojo-type="dijit.Menu" id="helpMenu">
          <div data-dojo-type="dijit.MenuItem">Help Topics</div>
          <div data-dojo-type="dijit.MenuItem">About Dijit</div>
        </div>
      </div>
      <div data-dojo-type="dijit.PopupMenuBarItem" data-dojo-props="disabled:true">
        <span>Disabled</span>
        <div data-dojo-type="dijit.Menu">
          <div data-dojo-type="dijit.MenuItem">You should not see this</div>
        </div>
      </div>
      <div data-dojo-type="dijit.MenuBarItem" data-dojo-props="onclick: function(){ alert('no submenu, just a clickable MenuItem'); }">
        Click me!
      </div>
    </div>
    
   */
  };
  
  return util;
});