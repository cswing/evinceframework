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
    "dojo/dom-construct",
    "dojo/topic",
    "dijit/_Widget",
    "dijit/_Templated",
    "evf/dataTypeFactory",
    "evf/store/util",
    "dojo/text!./templates/Editor.html"
], function(declare, domConstruct, topic, Widget, Templated, dataTypeFactory, storeUtil, template) {

    return declare("evf.properties.Editor", [Widget, Templated], {
        
        templateString: template,
        
        baseClass: 'defaultEditor',
        
        store:      null,
        // summary: 
        //      the store that contains the metadata that describes the properties and categories.
        
        readOnly:   false,
        // summary:
        //      whether or not the editor should display the properties in read mode or edit mode.
        
        autoBind: false,
        // summary:
        //      If true, the value displayed to the user will be updated when the backing item in the 
        //      store is changed.  When readOnly is false,   the item should be updated when the value 
        //      of the widget is changed.
        
        item:       null,
        // summary:
        //
     
        _propertyMap: null,
        
        postMixInProperties: function(){
            this.inherited(arguments);
            
            if(this.store == null)
                throw 'a store is required.';
            
            if(this.item == null)
                throw 'an item is required.';
                
            this._propertyMap = {};
        },
        
        postCreate: function(){
            this.inherited(arguments);
            dojo.addClass(this.domNode, this.baseClass);
            
            // TODO sort categories
            this.store.query({_type:"propertyCategory"}).forEach(dojo.hitch(this, this._processCategory));
        },
        
        _setItemAttr: function(item) {
            this.item = item;
            if(this.hndl){
                topic.unsubscribe(this.hndl);
                delete this.hndl;
            }
        
            if (this.autoBind){
                this.hndl = storeUtil.subscribe(this.item, dojo.hitch(this, this._onItemChange));
            }
        },
        
        _onItemChange: function(item, name){
            for(var id in this._propertyMap){
                var meta = this._propertyMap[id];
                var key = meta.property.key;
                if(key == name){
                    // TODO propertyGetter to get the value from the data object
                    var value = this.item[key];
                    this._updateProperty(meta, value);
                }
            }
        },
        
        _updateProperty: function(meta, newValue){
            var dataType = meta.dataType;
            if(this.readOnly){
                meta.valueSpan.innerHTML = dataType.format(newValue);
            } else{
                dataType.setValue(meta.widget, newValue);
            }
        },
        
        _processCategory: function(category, idx, allCategories){
            var td = domConstruct.create('td', { colspan: 2 }, 
                domConstruct.create('tr', {}, this.editorBodyNode));
            this._applyIndexedStyle(td, idx, allCategories, 'categoryCell');
            
            var plusNode = domConstruct.create('div', {}, td);
            dojo.addClass(plusNode, 'category categoryVisible');
            // TODO wire up navigation
            
            var title = domConstruct.create('span', {}, td);
            title.innerHTML = category.name;
            
            // TODO sort properties
            this.store.query({_type:"property", category: category}).forEach(dojo.hitch(this, this._processProperty));
        },
        
        _processProperty: function(property, idx, allCategoryProperties){
            var tr = domConstruct.create('tr', {}, this.editorBodyNode);
            this._applyIndexedStyle(tr, idx, allCategoryProperties, 'propertyRow');
            
            var tdCaption = domConstruct.create('td', {}, tr);
            dojo.addClass(tdCaption, 'caption');
            tdCaption.innerHTML = property.name;
            
            var tdEditor = domConstruct.create('td', {}, tr);
            dojo.addClass(tdEditor, 'editor');
            
            var dataType = dataTypeFactory.findDataType(property.dataType);
            if(!dataType)
                throw 'unknown dataType';
                
            var meta = {
                row:            tr,
                captionCell:    tdCaption,
                valueCell:      tdEditor,
                property:       property,
                dataType:       dataType,
                connects:       []
            };
            this._propertyMap[property.id] = meta;
            
            // TODO propertyGetter to get the value from the data object
            var value = this.item[property.key];
                
            if(this.readOnly){
                var span = domConstruct.create('span', {}, tdEditor);
                span.innerHTML = dataType.format(value);
                meta.valueSpan = span;
            
            } else{
                var context = dojo.mixin({}, property);
                context.dataItem = this.item;
                context.value = value;
                
                var node = domConstruct.create('div', {}, tdEditor);
                var w = dataType.createWidget(node, context);
                meta.widget = w;
                meta.connects.push(
                    this.connect(meta.widget, 'onChange', function(newValue) {                                
                        if(this.autoBind){
                            if(this.item[property.key] != newValue){
                                // TODO propertySetter to set the value on to the data object
                                this.item[property.key] = newValue;
                                storeUtil.publish(this.item, property.key);
                            }
                        }
                        this.onValueChanged(meta.property, newValue);
                    })
                );
            }
        },
        
        onValueChanged: function(property, newValue){},
        
        updateBackingItem: function(/*Object?*/ obj){
            var item = obj || this.item;
            
            for(var id in this._propertyMap){
                var meta = this._propertyMap[id];
                var key = meta.property.key;
                
                // TODO propertySetter to set the value on to the data object
                item[key] = meta.widget.get('value');
                storeUtil.publish(item, key);
            }
        },
        
        // TODO move this to a utililty place
        _applyIndexedStyle: function(node, idx, dataArray, defaultCss) {
            
            var cls = [];
            cls.push(defaultCss);
            
            if(idx==0){
                dojo.addClass(node, 'first');
            }
            
            cls.push(idx%2==0 ? 'odd' : 'even')
            
            if(idx==dataArray.length-1){
                dojo.addClass(node, 'last');
            }
            
            dojo.addClass(node, cls.join(' '));
        }
        
    });
});