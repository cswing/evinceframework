define("example/OwnersListPageController", [
  "dojo", "dijit", "dojo/dom-construct",  
  "evf/layout/lob/ListPageController", "dijit/MenuItem",
  "example/_ControllerMixin"
], function(dojo, dijit, domConstruct, ListPageController, MenuItem, ControllerMixin) {

	return dojo.declare("example.OwnersListPageController", [ListPageController, ControllerMixin], {	
		
		buildActions: function(data, dropDown) {
			var _ctrl = this;
			var editAction = new MenuItem({
              label: 'Edit'  
            });
			editAction.connect(editAction, 'onClick', function() {
				
			});
			
			dropDown.addChild(editAction);
        },
        
        getStructure: function() {
          return [
            this.createMenuCellDef(),
            { field: "name",    caption: "Name" },
            { field: "address", caption: "Address" },
            { field: "city",    caption: "City" },
            { field: "phone",	caption: "Telephone" },
            { field: "pets",	caption: "Pets" }
          ];  
        }
	  
	});
});