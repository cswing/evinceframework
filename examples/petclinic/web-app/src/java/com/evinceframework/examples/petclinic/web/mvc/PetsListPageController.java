package com.evinceframework.examples.petclinic.web.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.evinceframework.data.Query;
import com.evinceframework.data.QueryParameters;
import com.evinceframework.data.impl.DefaultQueryParametersImpl;
import com.evinceframework.examples.petclinic.Pet;
import com.evinceframework.examples.petclinic.web.navigation.SiteNavigationProvider;
import com.evinceframework.web.dojo.mvc.model.ViewModelUtils;
import com.evinceframework.web.dojo.navigation.NavigationCommand;
import com.evinceframework.web.dojo.navigation.NavigationProvider;
import com.evinceframework.web.dojo.navigation.Navigator;
import com.evinceframework.web.stereotype.PageController;

@Controller
@PageController
@RequestMapping(SiteNavigationProvider.PETS_LIST_URL)
public class PetsListPageController {

	public static final String LIST_VIEW = "pets.list";
	
	private ContextNavigation contextNavigation = new ContextNavigation();
	
	private ViewModelUtils modelUtil = new ViewModelUtils();
	
	@Autowired
	@Qualifier(value="pets")
	private Query<Pet> query;
	
	@RequestMapping(method=RequestMethod.GET)
	public String defaultDisplay(ModelMap model) {
		
		modelUtil.addToViewModel(model, contextNavigation.getNavigator()); 
		
		QueryParameters params = new DefaultQueryParametersImpl(); 
		// TODO get defaults from personalization?
		
		modelUtil.addToViewModel(model, query.execute(params));
		
		return LIST_VIEW;
	}
	
	public class ContextNavigation implements NavigationProvider {

		@Override
		public Navigator getNavigator() {
			
			Navigator nav = new Navigator();
			nav.setSiteNavigation(false);
			
			NavigationCommand addCommand = new NavigationCommand();
			addCommand.setOrder(0);
			addCommand.setTitle("New Pet");
			nav.getItems().add(addCommand);
			
			return nav;
		}
	}
}
