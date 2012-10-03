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
  "dojo/_base/lang",
  "dojo/dom-class",
  "dijit/layout/BorderContainer"
], function(declare, lang, domClass, Border){

    return declare("evf.layout.standard.StandardPage", [Border], {
    
        viewModel:      null,
        
        controller:     null,
        
        liveSplitters:  false,
        
        gutters:        false,
        
        design:         'headline',
        
        postMixInProperties: function(){
            this.inherited(arguments);
            if (!this.controller)
                throw 'controller was not defined';
        },
        
        postCreate: function() {
            this.inherited(arguments);
            domClass.add(this.domNode, 'standardPage');
            
            var Controller = this.controller;
            if(lang.isString(this.controller))
            	this.controller = lang.getObject(this.controller, false);
             
            this.controller = new Controller({
                page:       this,
                viewModel:  this.viewModel
            });
            this.controller.createLayout(this);
        }
        
    });/*declare*/
});/*define*/