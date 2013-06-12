/*
 * Copyright 2013 the original author or authors.
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
 define([
 	'dojo/_base/config',
	'dojo/_base/declare',
	'dojo/request/xhr',
	'evf/serviceRegistry',
	'./topics',
	'dojo/i18n!./nls/membership'
], function(config, declare, xhr, serviceRegistry, topics, i18n){

	var contextPath = (config.contextPath ? config.contextPath : '');
	
	var authenticationUrl = contextPath + '/security/authenticate';
	serviceRegistry.registerServicePath(authenticationUrl);
	
	var logoutUrl = contextPath + '/security/logout';
	

	return declare('evfMembership._ServiceMixin', [/*assumes ComplexWidget*/], {
		// summary:
		// 		A mixin design to be included with the evf/layout/twib/_Layout to provide server communication 
		//		for authentication and profile that is part of the evf-membership library.
		
		startup: function() {
			this.inherited(arguments);

			this.subscribe(topics.requestAuthentication, 'authenticate');
			this.subscribe(topics.logout, 'logout');
			this.subscribe(topics.viewProfile, 'viewProfile');
		},

		authenticate: function(data) {
				
			xhr(authenticationUrl, {
				method: 'POST',
				preventCache: true,
				data: {
					emailAddress: data.loginId,
					password: data.password
				}
			}).then(this.hitch(function(response) {
				var fn = data.callback;
				if(fn && this.dojoLang.isFunction(fn))
					fn(response);
			}));
		},

		logout: function() {
			window.location = logoutUrl;
		},

		viewProfile: function() {
			throw 'viewProfile is not implemented.';
		}
	});
});