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
	'dojo/aspect',
	'dojo/Deferred',
	'dojo/request/xhr',
	'dojo/store/Observable',
	'dojo/topic',
	'dijit/_TemplatedMixin',
	'dijit/Dialog',
	'./ComplexWidget',
	'./layout/topics',
	'./store/ViewModel'
], function(require, array, config, declare, aspect, Deferred, xhr, Observable, dojoTopic, 
		Templated, Dialog, ComplexWidget, layoutTopics,  ViewModel) {

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

	exports.ackknoledgeMessage = function(msg) {
		msg._devAck = true;
	};

	/*
		Use aop on xhr requests -> when hitting contextPath/json/..., 

		before calling service, notify to clear generic error messages

		on return from service
		before -> 
			- take the data and create a ViewModel
			- query view model for system errors and handle. 
			- do not continue on system error

		after -> 
			- query the viewModel for any user messages that haven't been devAckd
			- notify generic error messages
	*/


	var jsonServicePath = config.contextPath + '/json/';
	exports.registerServicePath(jsonServicePath);
	
	var dlg, content;
	var showSystemErrors = function(errs) {

		if(!dlg) {
			dlg = new Dialog({
				title: exports._systemErrorCode,
				content: content = new exports._systemErrorContentClass({})
			});
			//content = dlg.get('content');
		}

		content.set('errors', errs);
		dlg.show();
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

					var vm = new Observable(new ViewModel({ data: response }));

					// query view model for system errors and handle. 
					var hasSystemError = false;
					var result = vm.query({ _type: 'evf.msg', code: exports._systemErrorCode });

					if(result.count > 0) {
						showSystemErrors(result);
						dfd.reject(result);
						return;
					}

					dfd.resolve(vm);

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
