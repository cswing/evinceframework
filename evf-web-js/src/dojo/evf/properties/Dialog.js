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
    "dijit/_Widget",
    "dijit/_Templated",
    "dijit/Dialog",
    "dijit/form/Button",
    "evf/properties/Editor",
    "dojo/text!./templates/DialogContent.html"
], function(declare, Widget, Templated, Dialog, Button, Editor, template){
    
    var DialogContent = declare("evf.properties.DialogContent", [Widget, Templated], {
        
        templateString: template,
        
        editor: null,
        
        postMixInProperties: function(){
            this.inherited(arguments);
            this._startupWidgets = [];
        },
        
        postCreate: function(){
            this.inherited(arguments);
            
            this.editor = new Editor({
                readOnly:   this.readOnly,
                autoBind:   this.autoBind,
                item:       this.item,
                store:      this.store
            }, this.editorNode);
            
            this.ok = new dijit.form.Button({
                label:	'OK' // TODO i18n
            }, this.okButtonNode);
            this._startupWidgets.push(this.ok);
            this.connect(this.ok, 'onClick', function(evt) { this._onOK(); });
            
            this.cancel = new dijit.form.Button({
                label:	'Cancel' // TODO i18n
            }, this.cancelButttonNode);
            this._startupWidgets.push(this.cancel);
            this.connect(this.cancel, 'onClick', function(evt) { this._onCancel(); });
        },
        
        _onOK: function(){},
        
        _onCancel: function(){}
        
    });
    
    return declare("evf.properties.Dialog", [Dialog], {
        
        content: null,
        
        // passthru values to the content widget
        readOnly:   false,
        item:       null,
        store:      null,
        
        postCreate: function(){
            this.inherited(arguments);
            
            dojo.addClass(this.domNode, "propertyDialog");
            
            this.content = new DialogContent({ 
                readOnly:   this.readOnly,
                autoBind:   false,
                item:       this.item,
                store:      this.store
            });
            this.set('content', this.content);
            
            this.connect(this, 'onDialogOK', function(){this._updateBackingItem();});
            this.connect(this.content, '_onOK', function(){
                try{
                    this.onDialogOK();
                } catch(err) {
                    return;
                }
                
                this._ok = true;
                this.hide();
            });
            this.connect(this.content, '_onCancel', this.hide);
        },
        
        hide: function(){
            this.inherited(arguments);
            
            if (this._ok !== true){
                this.onDialogCancel();
            }
            delete this._ok;
        },
        
        _updateBackingItem: function(){ 
            if (this.readOnly)
                return;
            
            var editor = this.content.editor;
            editor.updateBackingItem();
        },
        
        onDialogOK: function() {},
        
        onDialogCancel: function() {}
        
    }); //declare Dialog
});/*define*/