define("example/OwnersListPageController", [
  "dojo", "dijit", "dojo/dom-construct",  
  "evf/layout/lob/ListPageController", "dijit/MenuItem",
  "example/_ControllerMixin", "evf/data/util"
], function(dojo, dijit, domConstruct, ListPageController, MenuItem, ControllerMixin, dataUtil) {

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
            { field: "name",    caption: "Name", renderCell: dataUtil.fullNameCellRenderer },
            { field: "address", caption: "Address" },
            { field: "city",    caption: "City" },
            { field: "telephone", caption: "Telephone" },
            { field: "pets",	caption: "Pets", renderCell: this.renderPets }
          ];  
        },
        
        renderPets: function(td, data, rowIdx, colIdx, cellDef) {
        	var names = [];
        	dojo.forEach(data.pets, function(s) {
        		names.push(s.name);
        	});
        	td.innerHTML = names.join(', ');
        }
	  
	});
});