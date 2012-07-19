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

import com.evinceframework.web.dojo.json.JsonStoreEngine;
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
	
	private JsonStoreEngine jsonEngine;
	
	private String[] storeNames;
	
	private DojoConfigurationResolver configurationResolver;
	
	private DojoLayout layout;
	
	@Override
	public String getContentType() {
		return DEFAULT_CONTENT_TYPE;
	}

	public DojoConfigurationResolver getConfigurationResolver() {
		return configurationResolver;
	}

	public DojoView(JsonStoreEngine jsonEngine, String[] storeNames, DojoConfigurationResolver configurationResolver, DojoLayout layout) {
		this.jsonEngine = jsonEngine;
		this.storeNames = storeNames;
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
		ctx.addRequires("evf.store.ViewModel");
		
		// add any requires that have been defined in the configuration
		ctx.getRequires().addAll(cfg.getRequires());

		writer.write(layout.getDocType(ctx));
		writer.write("\n<html>\n<head>\n");
		
		layout.renderHeadContent(writer, ctx);
		
		writer.write("\n\n</head>\n");
		writer.write(String.format("<body class=\"%s\">\n\n", cfg.getBodyCss()));
	
		layout.renderBodyContent(writer, ctx);
		
		// Render the main script with the configuration
		writer.write("\n\n<script type=\"text/javascript\" ");
		writer.write(String.format(" src=\"%s\"", 
				PathUtils.buildScriptPath(ctx, cfg.getCoreDojoPath())));
		writer.write(String.format("\n data-dojo-config=\"\n%s\n\" ",
				createDojoConfig(cfg)));
		writer.write(">\n</script>\n");
		
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
		dojoConfig.append("\tparseOnLoad: false");
		
		if (params != null) {
			for(String k : cfg.getConfigParameters().keySet()) {			
				
				if ("parseOnLoad".equals(k))
					continue;
				
				dojoConfig.append(String.format(",\n\t%s: '%s'", k, params.get(k)));
			}
		}
		
		Map<String, String> modules = cfg.getModules();
		if (modules != null && modules.size() > 0) {
			dojoConfig.append(",\n\tpaths: {");
			boolean needComma = false;
			for(String key : modules.keySet()) {
				if (needComma) {
					dojoConfig.append(",");
				} else {
					needComma = true;
				}
				
				dojoConfig.append(String.format("\n\t\t%s: '%s'", key, modules.get(key)));
			}
			dojoConfig.append("\n\t}");
		}
		
		return dojoConfig.toString();
	}
	
	protected void renderInitializationCode(PrintWriter writer, DojoViewRenderingContext ctx, DojoConfiguration cfg) {
		
		writer.write("<script type=\"text/javascript\">\n");
		
		writer.write("require(['dojo/domReady!'");
		for(String require : ctx.getRequires()) {
			writer.write(", '");
			writer.write(require.replace('.', '/'));
			writer.write("'");
		}
		writer.write("], function() {");
		writer.write(String.format("\n\tdojo.setObject('contextPath', '%s')\n", ctx.getRequest().getContextPath()));
		
		for (String storeName : storeNames) {
			Object obj = ctx.getModel().get(storeName);
			writer.write(String.format("\n\tdojo.setObject('%s', new evf.store.ViewModel({data: %s}));", storeName, jsonEngine.serialize(obj)));
		}
		
		layout.renderPreParseJs(writer, ctx);
		writer.write("\n\t\tdojo.parser.parse();\n");
		layout.renderPostParseJs(writer, ctx);
		
		writer.write("});\n");
	}
	
}
