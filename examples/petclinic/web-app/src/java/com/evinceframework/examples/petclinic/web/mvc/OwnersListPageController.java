package com.evinceframework.examples.petclinic.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.evinceframework.examples.petclinic.web.navigation.SiteNavigationProvider;
import com.evinceframework.web.dojo.mvc.model.ViewModelUtils;
import com.evinceframework.web.dojo.navigation.NavigationCommand;
import com.evinceframework.web.dojo.navigation.NavigationProvider;
import com.evinceframework.web.dojo.navigation.Navigator;
import com.evinceframework.web.stereotype.PageController;

@Controller
@PageController
@RequestMapping(SiteNavigationProvider.OWNER_LIST_URL)
public class OwnersListPageController {

	public static final String LIST_VIEW = "owners.list";
	
	private ContextNavigation contextNavigation = new ContextNavigation();
	
	private ViewModelUtils modelUtil = new ViewModelUtils();
	
	@RequestMapping(method=RequestMethod.GET)
	public String defaultDisplay(ModelMap model) {
		
		modelUtil.addToViewModel(model, contextNavigation.getNavigator()); 
		
		return LIST_VIEW;
	}
	
	public class ContextNavigation implements NavigationProvider {

		@Override
		public Navigator getNavigator() {
			
			Navigator nav = new Navigator();
			nav.setSiteNavigation(false);
			
			NavigationCommand addCommand = new NavigationCommand();
			addCommand.setOrder(0);
			addCommand.setTitle("New Owner");
			nav.getItems().add(addCommand);
			
			return nav;
		}
		
	}
}
