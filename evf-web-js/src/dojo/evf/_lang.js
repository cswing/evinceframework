/*
 * Copyright 2012-2013 the original author or authors.
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
define(["dijit/form/_TextBoxMixin", "dijit/form/TextBox"], function(_TextBoxMixin, TextBox) {

	// String enhancements
	String.prototype.startsWith = function(str) { 
		return this.slice(0, str.length) == str;
	};
	
	String.prototype.endsWith = function(str) { 
		return this.slice(-str.length) == str;
	};
	
	String.prototype.contains = function(str) {
		return this.indexOf(str) != -1;
	};

	// Array enhancements
	Array.prototype.clone = function() { return this.slice(0); }    

	/*
	 * http://www.tutorialspoint.com/javascript/array_indexof.htm
	 *
	 * This method is a JavaScript extension to the ECMA-262 standard; as such it may not 
	 * be present in other implementations of the standard. 
	 */
	if (!Array.prototype.indexOf) {
		Array.prototype.indexOf = function(elt /*, from*/) {
			var len = this.length;

			var from = Number(arguments[1]) || 0;
			from = (from < 0) ? Math.ceil(from) : Math.floor(from);
			if (from < 0)
				from += len;

			for (; from < len; from++) {
			if (from in this &&
				this[from] === elt)
				return from;
			}
			return -1;
		};
	}

	/*
	 * Modify Dojo's default behavior regarding the HTML5 placeHolder - if focused and 
	 * still empty, then the placeholder should still be shown.
	 */
	TextBox.prototype._updatePlaceHolder = function(){
		if(this._phspan){
			this._phspan.style.display=(this.placeHolder&&!this.textbox.value)?"":"none";
		}
	};
	TextBox.prototype._processInput = function(){
		_TextBoxMixin.prototype._processInput.apply(this, arguments);
		this._updatePlaceHolder();
	};

});