/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evinceframework.web.dojo.mvc.view;

import java.io.PrintWriter;
import java.util.Set;

/**
 * A {@link DojoView} will delegate to an implementation of this interface to provide 
 * functionality when rendering an html page.
 * 
 * @author Craig Swing
 */
public interface DojoLayout {

	public String getDocType(DojoViewRenderingContext ctx);
	
	public Set<String> getInitializationRequires();
	
	public void renderHeadContent(PrintWriter writer, DojoViewRenderingContext ctx);
	
	public void renderBodyContent(PrintWriter writer, DojoViewRenderingContext ctx);
	
	/**
	 * Allow a layout to render javascript after Dojo has been configured.  This method should emit
	 * javascript functions that take the form of the AMD format.
	 * 
	 * <code>
	 * 	require([...], function(...) {
	 * 		// code here
	 *  });
	 * </code>
	 * 
	 * @param writer
	 * @param ctx
	 */
	public void renderJavascript(PrintWriter writer, DojoViewRenderingContext ctx);
}
