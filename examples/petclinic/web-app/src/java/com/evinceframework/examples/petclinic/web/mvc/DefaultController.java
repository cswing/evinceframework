package com.evinceframework.examples.petclinic.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.evinceframework.web.stereotype.PageController;

@Controller
@PageController
@RequestMapping("/")
public class DefaultController {

	public static final String DEFAULT_VIEW = "default.landing";
	
	@RequestMapping(method=RequestMethod.GET)
	public String defaultDisplay(ModelMap model) {
		return DEFAULT_VIEW;
	}
	
}
