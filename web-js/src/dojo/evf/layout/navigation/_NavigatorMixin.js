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
define("evf/layout/navigation/_NavigatorMixin", [
  "dojo", "dijit", "dojo/dom-construct",
  "evf/_lang", "evf/store/util", "evf/dialog/util",
  "dijit/_Widget", "dijit/_Container"
], function(dojo, dijit, domConstruct, eLang, 
		storeUtils, dialogUtils, Widget, Container) {

	dojo.declare("evf.layout.navigation._NavigatorPaneMixin", null, {
		
		navigatorItem: null,
		
		postCreate: function() {
			this.inherited(arguments);
			
			var parentNode = domConstruct.create('ul', { 'class': 'itemGroup' }, this.domNode);
			this.processItems(this.navigatorItem, parentNode);
		},
		
		processItemWithChildren: function(navItem, parentNode) {
			var span = domConstruct.create('span', { 'class': 'itemGroupTitle' }, parentNode);
			span.innerHTML = navItem.title;
	          
			var ul = domConstruct.create('ul', { 'class': 'itemGroup' }, parentNode);
			this.processItems(navItem, ul);
		},
		
		createItem: function(navItem, parentNode) {

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
	            this.connect(a, 'onclick', function() {
	            	dialogUtils.showConfirmation('Are you sure?');
		        });
	        }
		},
		
		processItems: function(parentItem, parentNode) {
			
			var items = storeUtils.sort(parentItem.items.clone(), 'order');
			
			dojo.forEach(items, function(navItem) {
				if (navItem.items) {
		        	this.processItemWithChildren(navItem, parentNode);
		        } else {
		        	this.createItem(navItem, parentNode);
		        }
			}, this);
		}
	});
	
	return dojo.declare("evf.layout.navigation._NavigatorMixin", null, { 
		
		navigator: null,
		
		postCreate: function() {
			this.inherited(arguments);
			
			var items = this.navigator.items || [];
		    items = storeUtils.sort(items.clone(), 'order');
		    dojo.forEach(items, this.createTopLevelWidget, this);
		},
		
		createTopLevelWidget: function(navItem) {
			throw 'createTopLevelWidget must be implemented by ' + this.declaredClass;
		}		
	});
	
});