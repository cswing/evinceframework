define("example/DefaultPageController", [
  "dojo", "dijit", "dojo/dom-construct",
  "dijit/_Widget", "dijit/DropDownMenu", "dijit/form/DropDownButton",
  "evf/layout/lob/ApplicationPageController", "dijit/layout/ContentPane",
  "example/_ControllerMixin"
], function(dojo, dijit, domConstruct, Widget, DropDownMenu, DropDownButton,
    ApplicationPageController, ContentPane, ControllerMixin) {

	return dojo.declare("example.DefaultPageController", [ApplicationPageController, ControllerMixin], {	
		
		createMainContent: function() {
			return new ContentPane({ content: 'Default Content' });
		}
	  
	});
});