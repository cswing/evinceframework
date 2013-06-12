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
	'dojo/_base/array',
	'dojo/_base/config',
	'dojo/_base/lang',
	'dojo/dom-construct',
	'dojo/on',
	'dijit/form/Button',
	'dijit/form/DropDownButton', 
	'dijit/DropDownMenu', 
	'dijit/MenuItem',
	'evf/store/util'
], function(array, config, lang, domConstruct, on, Button, DropDownButton, DropDownMenu, MenuItem, storeUtils){

    var util = {};
    
    util.buildNavigation = function(nav, node, popupCss){
        
        var widgets = [];
        var items = nav.items || [];
        items = storeUtils.sort(items.clone(), 'order');
        
        array.forEach(items, function(navItem, idx) {
            
            var div = domConstruct.create('div', {}, node);
            
            if(navItem.items)
                widgets.push(util._createDropDownButton(navItem, div, popupCss));
            else
                widgets.push(util._createButton(navItem, div));
            
        }, this);
        
        return widgets;
    };
    
    util._createDropDownButton = function(navItem, node, popupCss){
        
        var menu = new DropDownMenu({ style: "display: none;" });
        if(popupCss)
            dojo.addClass(menu.domNode, popupCss);
        
        var fnDoWithNavItem = lang.hitch(this, function (item) {
            var menuItem = new MenuItem({
            	label: item.title
            });
            menu.addChild(menuItem);
            
            if (item.url) {
	        	var url = item.url;
	        	if (!url.startsWith("http")) {
	        		var ctxPath = config.contextPath;

	        		if (ctxPath && ctxPath != '') {
	        			url = lang.replace("{0}/{1}", [ctxPath, url]).replace('//', '/');
	        		}
	        	}
	        	//var a = domConstruct('a', {innerHTML: item.title}, menuItem.containerNode);
	        	//dojo.attr(a, 'href', url);
	        	
	        	on(menuItem, 'click', function(){ window.location = url;});
	        }
        });
        
        navItem.items.forEach(fnDoWithNavItem);
        
        return new DropDownButton({
            label: navItem.title,
            dropDown: menu
        }, node);
    };
    
    util._createButton = function(navItem, node){
        return new Button({ 
            label: navItem.title 
        }, node);
    };

    lang.setObject("evf.layout.navigation.menuUtil", util);
    return util;
});