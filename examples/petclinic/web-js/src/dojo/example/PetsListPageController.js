define("example/PetsListPageController", [
  "dojo", "dijit", "dojo/dom-construct",  
  "evf/layout/lob/ListPageController", "dijit/MenuItem",
  "example/_ControllerMixin"
], function(dojo, dijit, domConstruct, ListPageController, MenuItem, ControllerMixin) {

	return dojo.declare("example.PetsListPageController", [ListPageController, ControllerMixin], {	
		
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
            { field: "birthDate", caption: "Birthday" },
            { field: "type",    caption: "Type" }
          ];  
        }
	  
	});
});