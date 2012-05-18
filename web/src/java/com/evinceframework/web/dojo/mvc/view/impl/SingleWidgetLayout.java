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
package com.evinceframework.web.dojo.mvc.view.impl;

import java.io.PrintWriter;

import org.springframework.util.StringUtils;

import com.evinceframework.web.dojo.mvc.view.DojoLayout;
import com.evinceframework.web.dojo.mvc.view.DojoViewRenderingContext;
import com.evinceframework.web.dojo.mvc.view.PathUtils;

/**
 * A SingleWidgetLayout is an implementation of {@link DojoLayout} that in conjunction with a
 * {@link DojoView} renders an html page where the body is a single Dojo widget.  This single
 * widget is responsible for building the page layout.
 * 
 * In addition to providing the single widget layout, the SingleWidgetLayout can also provide
 * the following: 
 * <ul>
 * 	<li>a static page title</li>
 * 	<li>a favorite icon</li>
 * 	<li>free form content that can be placed in the head</li>
 * </ul>	
 * 
 * @author Craig Swing
 */
public class SingleWidgetLayout implements DojoLayout {

	private static final String HTML5_DOC_TYPE = "<!DOCTYPE html>";

	private String pageTitle = null;
	
	private String headerContent = null;
	
	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getHeaderContent() {
		return headerContent;
	}

	public void setHeaderContent(String headerContent) {
		this.headerContent = headerContent;
	}

	@Override
	public String getDocType(DojoViewRenderingContext ctx) {
		return HTML5_DOC_TYPE;
	}

	@Override
	public void renderHeadContent(PrintWriter writer, DojoViewRenderingContext ctx) {
		
		// output favorite icon, if one is defined
		if (StringUtils.hasLength(ctx.getConfiguration().getFavoriteIconLink())) {
			writer.write(String.format("<link rel=\"icon\" type=\"image/x-icon\" href=\"%s\"/>\n",
					PathUtils.buildScriptPath(ctx, ctx.getConfiguration().getFavoriteIconLink())));			
		}
		
		if (StringUtils.hasLength(getPageTitle())) {
			writer.write(String.format("<title>%s</title>", getPageTitle()));
		}
		
		for(String css : ctx.getConfiguration().getStylesheets()) {			
			writer.write(String.format(
					"<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\"/>\n", 
					PathUtils.buildScriptPath(ctx, css)));			
		}
		
		if (StringUtils.hasLength(getHeaderContent())) {
			writer.write(getHeaderContent());
		}
	}

	@Override
	public void renderBodyContent(PrintWriter writer, DojoViewRenderingContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderPreParseJs(PrintWriter writer, DojoViewRenderingContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderPostParseJs(PrintWriter writer, DojoViewRenderingContext ctx) {
		// TODO Auto-generated method stub
		
	}

}
