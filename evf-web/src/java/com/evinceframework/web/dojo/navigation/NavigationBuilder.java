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

/**
 * Modify an existing {@link Navigator}.  THis interface should be called by implementations of
 * {@link NavigationProvider}s when wanting to defer parts of the navigation creation process.
 * 
 * @author Craig Swing
 * @see NavigationProviderImpl
 */
public interface NavigationBuilder {

	public void buildNavigation(Navigator navigator);
	
}
