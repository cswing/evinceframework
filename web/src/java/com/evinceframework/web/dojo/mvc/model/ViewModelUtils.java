package com.evinceframework.web.dojo.mvc.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.ModelMap;
import org.springframework.util.ClassUtils;

public class ViewModelUtils {

	public static final String VIEW_MODEL = "viewModel";
	
	@SuppressWarnings("unchecked")
	public List<Object> findOrCreateViewModel(ModelMap model) {
		
		if (model.containsKey(VIEW_MODEL)) {
			
			Object obj = model.get(VIEW_MODEL);
			
			if (!ClassUtils.isAssignableValue(List.class, obj)) {
				throw new RuntimeException(String.format("%s is in the model, but is not a List", VIEW_MODEL));
			}
			
			return (List<Object>)obj;
		}
		
		List<Object> viewModel = new ArrayList<Object>();
		model.put(VIEW_MODEL, viewModel);
		return viewModel;
	}
	
}
