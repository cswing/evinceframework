package com.evinceframework.web.dojo.mvc.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.ui.ModelMap;
import org.springframework.util.ClassUtils;

public class ViewModelUtils {

	public static final String VIEW_MODEL = "viewModel";
	
	@SuppressWarnings("unchecked")
	public Set<Object> findOrCreateViewModel(ModelMap model) {
		
		if (model.containsKey(VIEW_MODEL)) {
			
			Object obj = model.get(VIEW_MODEL);
			
			if (!ClassUtils.isAssignableValue(Set.class, obj)) {
				throw new RuntimeException(String.format("%s is in the model, but is not a Set", VIEW_MODEL));
			}
			
			return (Set<Object>)obj;
		}
		
		Set<Object> viewModel = new HashSet<Object>();
		model.put(VIEW_MODEL, viewModel);
		return viewModel;
	}
	
	public void addToViewModel(ModelMap model, Object obj) {
		findOrCreateViewModel(model).add(obj);
	}
	
}
