package com.evinceframework.examples.petclinic.web.navigation;

import com.evinceframework.web.dojo.navigation.NavigationCategory;
import com.evinceframework.web.dojo.navigation.NavigationCommand;
import com.evinceframework.web.dojo.navigation.NavigationProvider;
import com.evinceframework.web.dojo.navigation.Navigator;

public class SiteNavigationProvider implements NavigationProvider {

	public static final String VET_LIST_URL = "/veterinarians/list";
	
	public static final String OWNER_LIST_URL = "/owners/list"; 
	
	public static final String PETS_LIST_URL = "/pets/list";
	
	@Override
	public Navigator getNavigator() {
	
		Navigator nav = new Navigator();
	
		NavigationCategory staff = new NavigationCategory();
		staff.setTitle("Office Staff");
		nav.getItems().add(staff);

		NavigationCommand listVetsCommand = new NavigationCommand(VET_LIST_URL);
		listVetsCommand.setOrder(0);
		listVetsCommand.setTitle("Veterinarians");
		staff.getItems().add(listVetsCommand);
		
		NavigationCategory customers = new NavigationCategory();		
		customers.setTitle("Owners & Pets");
		nav.getItems().add(customers);

		NavigationCommand listOwnersCommand = new NavigationCommand(OWNER_LIST_URL);
		listOwnersCommand.setOrder(0);
		listOwnersCommand.setTitle("Owners");
		customers.getItems().add(listOwnersCommand);	
		
		NavigationCommand listPetsCommand = new NavigationCommand(PETS_LIST_URL);
		listPetsCommand.setOrder(1);
		listPetsCommand.setTitle("Pets");
		customers.getItems().add(listPetsCommand);	
		
		NavigationCategory schedule = new NavigationCategory();
		schedule.setTitle("Schedule");
		schedule.setImplementation("example.navigation.ScheduleNavigator");
		nav.getItems().add(schedule);
		
		return nav;
	}
	
}
