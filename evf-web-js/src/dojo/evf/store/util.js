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
define("evf/store/util", [
  "dojo", "dijit", "dojo/dom-construct"
], function(dojo, dijit, domConstruct) {

  var util = dojo.getObject('evf.store.util', true);
  
  util.sort = function(array, sortDefinitions) {
    // summary:
    //  provide a utility method to sort an array.
    // description:
    //  extracted from dojo.store.util.SimpleQueryEngine
    
    var sorts = sortDefinitions;
    if (dojo.isString(sorts)) {
      sorts = [{ attribute: sorts }]
    }
    
    array.sort(function(a, b){
      for(var sort, i=0; sort = sorts[i]; i++){
        var aValue = a[sort.attribute];
        var bValue = b[sort.attribute];
        if (aValue != bValue) {
          return !!sort.descending == aValue > bValue ? -1 : 1;
        }
      }
      return 0;
    });
    
    return array;
  };
  
  return util;
});