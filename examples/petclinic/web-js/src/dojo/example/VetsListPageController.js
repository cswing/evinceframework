define("example/VetsListPageController", [
  "dojo", "dijit", "dojo/dom-construct",  
  "evf/layout/lob/ListPageController", "dijit/MenuItem",
  "example/_ControllerMixin", "evf/data/util"
], function(dojo, dijit, domConstruct, ListPageController, MenuItem, ControllerMixin, dataUtil) {

	return dojo.declare("example.VetsListPageController", [ListPageController, ControllerMixin], {	
		
		getServiceUrl: function() {
			return dojo.replace("{0}/json/data/query/vets", [dojo.getObject('contextPath')]);
		},
		
		buildActions: function(data, dropDown) {
			/*
			var _ctrl = this;
			var editAction = new MenuItem({
              label: 'Edit'  
            });
			editAction.connect(editAction, 'onClick', function() {
				
			});
			
			dropDown.addChild(editAction);
			*/
        },
        
        getStructure: function() {
          return [
            this.createMenuCellDef(),
            { field: "name",    caption: "Name", renderCell: dataUtil.fullNameCellRenderer, sortable: true, customSort: [ 'lastName', 'firstName' ] },
            { field: "specialties", caption: "Specialties", renderCell: this.renderSpecialties, sortable: false }
          ];  
        },
        
        renderSpecialties: function(td, data, rowIdx, colIdx, cellDef) {
        	var names = [];
        	dojo.forEach(data.specialties, function(s) {
        		names.push(s.name);
        	});
        	td.innerHTML = names.join(', ');
        }
	  
	});
});