/*
 * Copyright 2013 Craig Swing.
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
	'dojo/_base/array',
	'dojo/_base/declare',
	'dijit/_TemplatedMixin',
	//'evf/layout/navigation/menuUtil',
	'evf/ComplexWidget',
	'evf/layout/topics', 
	'dojo/text!./templates/NavToolbar.html'
], function(array, declare, Templated, /*menuUtils,*/ ComplexWidget, layoutTopics, template){

	return declare('evf.layout.twib.NavToolbar', [ComplexWidget, Templated], {

		templateString: template,

		viewModel: null,

		userNode: null,

		postCreate: function(){
			this.inherited(arguments);
		},

		startup: function() {
			if(this._started) return;
			this.inherited(arguments);

			this.listen(this.logoNode, 'click', function() {
				this.publish(layoutTopics.goHome, {});
			});
		}
	});
});