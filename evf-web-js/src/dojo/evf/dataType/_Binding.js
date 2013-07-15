/*
 * Copyright 2012-3 Craig Swing.
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
	'dojo/_base/lang',
	'dojo/aspect',
	'dojo/on'
], function(array, declare, lang, aspect, on) {

	return declare('evf.dataType._Binding', [], {

		_handles: null,

		widget: null,

		observable: null, 
		
		dataObject: null, 
		
		bindingKey: null,

		postscript: function(/*Object?*/params) {
			if(params) {
				lang.mixin(this, params);
			}
			this.bind();
		},

		bind: function() {

			this.unbind();

			this._handles.push(aspect.after(this.widget, 'destroy', lang.hitch(this, 'unbind')));
			this._handles.push(on(this.widget, 'change', lang.hitch(this, '_onWidgetChange')));

			// TODO support two way databinding using observable
		},

		unbind: function() {
			array.forEach(this._handles, function(hndl) { hndl.remove(); });
			this._handles = [];
		},

		_onWidgetChange: function(value) {
			lang.setObject(this.bindingKey, value, this.dataObject);
		}
	});

});