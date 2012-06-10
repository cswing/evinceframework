define("example/navigation/ScheduleNavigator", [
  "dojo", "dijit", "dojo/dom-construct", "dijit/_Widget", "dijit/Calendar"
], function(dojo, dijit, domConstruct, Widget, Calendar) {

	return dojo.declare("example.navigation.ScheduleNavigator", [Widget], {	
		
		calendar: null,
		
		postCreate: function() {
			this.inherited(arguments);
			
			this.calendar = new Calendar({}, 
					domConstruct.create('div', {}, this.domNode));
		}
		
	});
});