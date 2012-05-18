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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.evinceframework.web.dojo.mvc.view.config.DojoConfiguration;
import com.evinceframework.web.dojo.mvc.view.config.DojoConfigurationResolver;

/**
 * Renders an HTML page that has the following structure where the content described between
 * pipes (|) is provided by the {@link DojoLayout} or {@link DojoConfiguration}. 
 * 
 * <code>
 * 	|DOCTYPE|
 * 	<html>
 * 		<head>
 * 		|HEAD CONTENT|
 *  	</head>
 *  	<body class="|BODY CSS|">
 *  		|BODY CONTENT|
 *  		<script src="dojo.js"></script>
 *  		
 *  		|DOJO INITIALIZATION CODE|
 *  	</body>
 * 	</html> 
 * </code>
 * 
 * @author Craig Swing
 */
public class DojoView implements View {
	
	private static final String DEFAULT_CONTENT_TYPE = "text/html";
		
	private DojoConfigurationResolver configurationResolver;
	
	private DojoLayout layout;
	
	@Override
	public String getContentType() {
		return DEFAULT_CONTENT_TYPE;
	}

	public DojoConfigurationResolver getConfigurationResolver() {
		return configurationResolver;
	}

	public void setConfigurationResolver(DojoConfigurationResolver configurationResolver) {
		this.configurationResolver = configurationResolver;
	}

	public DojoView(DojoConfigurationResolver configurationResolver, DojoLayout layout) {
		this.configurationResolver = configurationResolver;
		this.layout = layout;
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				
		DojoViewRenderingContext ctx = new DojoViewRenderingContext(
				getConfigurationResolver().resolve(model, request, response), model, request, response);
		DojoConfiguration cfg = ctx.getConfiguration();
		
		PrintWriter writer = response.getWriter();
		
		ctx.addRequires("dojo.parser");
		
		// add any requires that have been defined in the configuration
		ctx.getRequires().addAll(cfg.getRequires());

		writer.write(layout.getDocType(ctx));
		writer.write("\n<html>\n<head>\n");
		
		layout.renderHeadContent(writer, ctx);
		
		writer.write("\n</head>\n");
		writer.write(String.format("<body class=\"%s\">\n", cfg.getBodyCss()));
	
		layout.renderBodyContent(writer, ctx);
		
		// Render the main script with the configuration
		writer.write(String.format(
				"\n\n<script type=\"text/javascript\" src=\"%s\" data-dojo-config=\"%s\"></script>\n",
				PathUtils.buildScriptPath(ctx, cfg.getCoreDojoPath()), createDojoConfig(cfg)));
		
		for(String path : cfg.getAuxiliaryScriptPaths()) {			
			writer.write(String.format(
					"<script type=\"text/javascript\" src=\"%s\"></script>\n", 
					PathUtils.buildScriptPath(ctx, path)));
		}
		
		renderInitializationCode(writer, ctx, cfg);
		
		writer.write("</script>\n");
		writer.write("\n</body>\n</html>");
	}
	
	protected String createDojoConfig(DojoConfiguration cfg) {
		
		Map<String, String> params = cfg.getConfigParameters();
		
		StringBuilder dojoConfig = new StringBuilder();
		dojoConfig.append("parseOnLoad: false");
		
		if (params != null) {
			for(String k : cfg.getConfigParameters().keySet()) {			
				
				if ("parseOnLoad".equals(k))
					continue;
				
				dojoConfig.append(String.format(", %s: '%s'", k, params.get(k)));
			}
		}
		
		return dojoConfig.toString();
	}
	
	protected void renderInitializationCode(PrintWriter writer, DojoViewRenderingContext ctx, DojoConfiguration cfg) {
		
		writer.write("<script type=\"text/javascript\">\n");
		
		// register module paths & requires
		writer.write("require(['dojo/_base/kernel', 'dojo/ready', 'dojo/parser', 'dojo/_base/loader], function(dojo, ready, parser){\n");
		
		if (cfg.getModules() != null && cfg.getModules().size() > 0) {
			for(String k : cfg.getModules().keySet()) {
				writer.write(String.format(
						"dojo.registerModulePath(\"%s\", \"%s\");\n", k, cfg.getModules().get(k)));
			}
		}
		
		StringBuilder requires = new StringBuilder(); 
		for(String require : ctx.getRequires()) {
			if(requires.length() > 0) {
				requires.append(",");
			}
			requires.append("'");
			requires.append(require.replace('.', '/'));
			requires.append("'");
		}
		
		writer.write(String.format("require([%s], function() {\n", requires.toString()));
		writer.write(String.format("dojo.setObject('contextPath', '%s')\n", ctx.getRequest().getContextPath()));
		
		// TODO render the view model
		
		layout.renderPreParseJs(writer, ctx);
		
		writer.write("\ndojo.parser.parse();\n");		
		
		layout.renderPostParseJs(writer, ctx);
		
		writer.write("});\n");
		writer.write("});\n");
	}
	
}
