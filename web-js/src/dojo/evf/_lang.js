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
define(["dojo"], function(dojo) {

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

});