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
define("evf/layout/navigation/AccordionNavigator", [
  "dojo", "dijit", "dojo/dom-construct",
  "evf/_lang", "evf/store/util", "evf/dialog/util",
  "evf/layout/navigation/_NavigatorMixin", 
  "dijit/layout/AccordionContainer", "dijit/layout/ContentPane"
], function(dojo, dijit, domConstruct, eLang, storeUtils, dialogUtils, 
		_NavigatorMixin, AccordionContainer, ContentPane ) {

	var Pane = dojo.declare("evf.layout.navigation._AccordionNavigatorPane", [ContentPane, evf.layout.navigation._NavigatorPaneMixin], { });
	
	return dojo.declare("evf.layout.navigation.AccordionNavigator", [AccordionContainer, _NavigatorMixin], { 
		
		postMixInProperties: function() {
			this.inherited(arguments);
		},
		
		postCreate: function() {
			this.inherited(arguments);
			dojo.addClass(this.domNode, 'navigation');
		},
		
		createTopLevelWidget: function(navItem) {
			var pane = new Pane({
		        title:      navItem.title,
		        navigatorItem: navItem
			});
			this.addChild(pane);
			return pane;
		}
		
	});
	
});