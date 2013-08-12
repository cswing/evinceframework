/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evinceframework.web.dojo.navigation;

import java.util.ArrayList;
import java.util.List;

/**
 * Default Implementation of {@link NavigationProvider} that defers to injected instances
 * of {@link NavigationBuilder}s.
 * 
 * @author Craig Swing
 *
 */
public class NavigationProviderImpl implements NavigationProvider {

	private List<NavigationBuilder> builders = new ArrayList<NavigationBuilder>();
	
	public List<NavigationBuilder> getBuilders() {
		return builders;
	}

	public void setBuilders(List<NavigationBuilder> builders) {
		this.builders = builders;
	}

	@Override
	public Navigator getNavigator() {
		
		Navigator nav = new Navigator();
		
		for(NavigationBuilder builder : builders) {
			builder.buildNavigation(nav);
		}
		
		return nav;
	}

}
