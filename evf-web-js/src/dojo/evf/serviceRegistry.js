/*
 * Copyright 2013 Craig Swing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0<div>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 define([
 	'require',
 	'dojo/_base/array',
	'dojo/_base/config',
	'dojo/_base/declare',
	'dojo/_base/lang',
	'dojo/aspect',
	'dojo/Deferred',
	'dojo/request/xhr',
	'dojo/store/Observable',
	'dojo/topic',
	'dijit/_TemplatedMixin',
	'dijit/Dialog',
	'./ComplexWidget',
	'./dialog/util',
	'./layout/topics',
	'./store/ViewModel'
], function(require, array, config, declare, lang, aspect, Deferred, xhr, Observable, dojoTopic, 
		Templated, Dialog, ComplexWidget, dialogUtils, layoutTopics,  ViewModel) {

	var SystemErrorDialogContent = declare([ComplexWidget, Templated], {

		templateString: '<div><div class="dijitInline icon"></div><div data-dojo-attach-point="containerNode" class="dijitInline messageContainer"></div></div>',

		postCreate: function() {
			this.inherited(arguments);
			this.domClass.add(this.domNode, 'evfSystemErrorDisplay');
		},

		startup: function() {
			if(this._started) return;
			this.inherited(arguments);

			this.set('errors', this.errors);
		},

		_setErrorsAttr: function(errs) {
			this.errors = errs;
			if(!this._started) return;

			this.domConstruct.empty(this.containerNode);

			var html = [];
			array.forEach(errs, function(err) {
				html.push(this.dojoLang.replace(
					'<div class="systemError"><span class="message">{message}</span><div class="description">{description}</div></div>', 
					err));
			}, this);
			this.domHtml.set(this.containerNode, html.join(''));
		}
	});

	var exports = {
		// summary:

		_servicePaths: [],

		_systemErrorCode: 'SYS-0000',

		_systemErrorContentClass: SystemErrorDialogContent
	};

	exports.registerServicePath = function(path){
		exports._servicePaths.push(path);
	};

	exports.isServiceUrl = function(url){
		// summary:
		//
		// url:
		//		
		if(!url) return false;

		return array.some(exports._servicePaths, function(path) { return url.startsWith(path); }); 
	};

	exports.acknowledgeMessage = function(msg) {
		msg._devAck = true;
	};

	var jsonServicePath = config.contextPath + '/json/';
	exports.registerServicePath(jsonServicePath);
	
	var dlg, content;
	var showSystemErrors = function(errs) {

		dialogUtils.showContentWithOkOnly(
			errs.length>0 ? errs[0].code : exports._systemErrorCode,
			function(node) {
				return new exports._systemErrorContentClass({
					errors: errs
				}, node); 
			});
	};

	aspect.around(require.modules['dojo/request/xhr'], 'result', function(originalXhr){
		return function(url, options, returnDeferred){

			// only concerned with xhr calls to the backend json services.
			if(!exports.isServiceUrl(url)) {
				return originalXhr(url, options, returnDeferred);
			}
			
			var dfd = new Deferred();

			options.handleAs = 'json';
			originalXhr(url, options, returnDeferred)
				.then(function(response) {

					// The web server does not send true http redirect codes, because some/most browsers 
					// will intercept and redirect the ajax request, when what is desired is to redirect the 
					// current page for the user.  So the server can send a json object that represents a 
					// redirect and it is automatically handled here.
					var isRedirect = response != null 
						&& lang.isObject(response) && !lang.isArray(response) && !lang.isFunction(response) 
						&& response.code && response.code == 302 && response.url && lang.isString(response.url);
					
					if (isRedirect) {
						window.location = response.url;
						return;
					}

					// It is assumed that the response from the server can be consumed using the ViewModel.
					// It is this ViewModel, that should get passed as the resolve method on the Deferred.
					var vm = new Observable(new ViewModel({ data: response }));

					// Determine if a SystemError occured and if so, show the error in a dialog window.
					var hasSystemError = false;
					var result = vm.query({ _type: 'evf.msg', code: exports._systemErrorCode });
					if(result.count > 0) {
						showSystemErrors(result);
						dfd.reject(result);
						return;
					}

					// At this point, the call was successful and Deferred should be resolved.
					dfd.resolve(vm);

					// After being resolved, determine if any user messages were not handled by the calling 
					// code.  These messages should be displayed to the user using the general user message 
					// mechanism.
					result = array.filter(vm.query({ _type: 'evf.msg' }),
						function(msg) {
							return msg._devAck !== true;
						});
					if(result.length > 0) {
						dojoTopic.publish(layoutTopics.messageNotification, {
								messages: result,
								clearPrevious: true
							});
					}
				
				}, function(err){

					var msgs = [{
						_type: 'evf.msg',
						msgType: 'error',
						code: exports._systemErrorCode,
						message: err,
						description: '',
						field: null
					}];

					// was expecting json but got something else instead
					if(err.message =='Unexpected token <') {
						msgs[0].code = 'HTTP-' + err.response.status
						msgs[0].message = 'Internal Server Error'; // i18n
					}
					
					showSystemErrors(msgs);
					dfd.reject(msgs);

				}, function(update) {
					dfd.progress(update);
				});


			return dfd;
		};
	});	
	
	return exports;
});
