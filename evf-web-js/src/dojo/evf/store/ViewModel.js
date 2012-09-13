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
define([
    "dojo/_base/declare",
    "dojo/date/stamp",
    "dojo/store/Memory"
], function(declare, dateStamp, Memory){

    /*=====
      var Memory = dojo.store.Memory;
    =====*/

    // module:
    //    evf/store/ViewModel
    // summary:
    //    Provides a more robust implementation of the dojo.Store API that can 
    //    be used to provide a view model.

    return declare("evf.store.ViewModel", [Memory], {
        // summary:
        //    An implementation of the dojo.Store API that supports a graph of data.
        //    
        
        idProperty: "_id",
	  
        referenceProperty: null,
        
        setData: function(data){
        
            // initialize customTypeMap Map.  This has been taken from dojo's IFRS
            if(!this.customTypeMap){
                this.customTypeMap = {};
                this.customTypeMap['Date'] = {
                    type: Date,
                    deserialize: function(value){
                        return dateStamp.fromISOString(value);
                    }
                };
            }
            
            if(data.items){
                this.idProperty = data.identifier;
                data = this.data = data.items;
            }else{
                this.data = data;
            }
            this.index = {};
            
            // when a reference to an item has not been registered,  
            // a setter function is added and will be called when 
            // the item is registered. 
            this._referenceSetters = {};
            
            // loop through the flat list of items
            dojo.forEach(this.data, function(itm, idx) {
                
                var itmId = itm[this.idProperty]; 
                this.index[itmId] = idx;
                
                for(var prop in itm) {
                    this._registerItem(itm, prop);  
                }
                
                // process the queue of deferred setters for this object.
                if (this._referenceSetters[itmId]) {
                    dojo.forEach(this._referenceSetters[itmId], function(fnSet) {
                        fnSet(itm);
                    });
                    this._referenceSetters[itmId] = null;
                }
            }, this);
            
            delete this._referenceSetters;
        },
        
        _inspectValue: function(val, fnSetter) {
          
            // Handle custom types
            var customTypeProperty = this.customTypeProperty || '_customType';
            var customValueProperty = this.customValueProperty || '_value';
            
            if(val && dojo.isObject(val) && val[customTypeProperty]){
                var customType = val[customTypeProperty];
                var mappingObj = this.customTypeMap[customType];
                if(!mappingObj)
                    throw 'Unknown customType: ' + customType;
                    
                fnSetter(mappingObj.deserialize(val[customValueProperty]));
                return;
            }
          
            var refProperty = this.referenceProperty || '_reference';
          
            // not a 'referece', so return the value.
            if (!val || !dojo.isObject(val) || !val[refProperty]) {
                fnSetter(val);
                return; 
            }
          
            var refId = val[refProperty];
            var refItm = this.get(refId);
          
            // the referenced item has already been loaded, return it. 
            if (refItm) {
                fnSetter(refItm); 
                return;
            }
          
            // we don't have the reference yet, qo queue a deferred setter that 
            // will set the value once the item is registered.
            if (! this._referenceSetters[refId]) {
                this._referenceSetters[refId] = [];
            }
            this._referenceSetters[refId].push(fnSetter); 
        },
      
        _registerItem: function(itm, prop) {
            var propValue = itm[prop];
              
            if (dojo.isArray(propValue)) {
                dojo.forEach(propValue, function(propItm, propIdx) {
                    this._inspectValue(propValue[propIdx], function(refItm) { 
                        propValue[propIdx] = refItm;  
                    });
                }, this);
            } else {
                this._inspectValue(itm[prop], function(refItm) { 
                    itm[prop] = refItm; 
                });
            }
        }
        
    }); //declare 
}); /* define */