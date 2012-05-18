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

import com.evinceframework.web.dojo.mvc.view.config.DojoConfiguration;

public class PathUtils {
	
	public static String buildScriptPath(DojoViewRenderingContext ctx, String scriptPath) {
		
		if (scriptPath.startsWith("http://") || scriptPath.startsWith("https://") || scriptPath.startsWith("//")) {
			return scriptPath;
		}
		
		DojoConfiguration cfg = ctx.getConfiguration();
		
		StringBuilder path = new StringBuilder();
		path.append(ctx.getRequest().getContextPath());
		path.append("/");
		path.append(scriptPath);
		
		if (path.indexOf("?") == -1) {
			path.append("?");
		} else {
			path.append("&");
		}
		path.append("preventCache=");
		path.append(cfg.getCacheKey());
		
		return path.toString();
	}
}
