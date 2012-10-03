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
  "dojo/_base/array",
  "dojo/_base/declare", 
  "dojo/dom-construct",
  "dojo/dom-geometry",
  "dijit/_Templated",
  "dijit/layout/ContentPane",
  "evf/layout/navigation/menuUtil",
  "evf/layout/standard/Toolbar",
  "dojo/text!./templates/_ContentPane.html"
], function(array, declare, domConstruct, domGeometry, Templated, ContentPane, menuUtils, Toolbar, template){

    var _ContentPane = declare("evf.layout.standard._ContentPane", [ContentPane, Templated], {
        
        templateString: template,
        
        controller:     null,
        
        postMixInProperties:function(){
            this.inherited(arguments);
            this.startupWidgets = [];
        },
        
        postCreate: function(){
            this.inherited(arguments);
            var _s = this;
            array.forEach(
                this.controller.createSecondaryNavigation(this.navigationNode),
                function(w){ _s.startupWidgets.push(w); }
            );
        },
        
        _layoutChildren: function(){
            
            var cn = this.containerNode;
            var mbDomNode = domGeometry.getMarginBox(this.domNode);
            var shDomNode = domGeometry.getMarginBox(this.subHeaderNode);
            
            dojo.style(cn, 'height', dojo.replace('{0}px', [mbDomNode.h - shDomNode.h]));
            this._contentBox = domGeometry.getContentBox(cn);
            
            this.inherited(arguments);
        }
    });

    return declare("evf.layout.standard.StandardPageController", [], {
        
        page: null,
        
        viewModel: null,
        
        constructor: function(params){
            if(params){
                this.params = params;
                dojo.mixin(this, params);
            }
            
            if (!this.page)
                throw 'page must be passed in.';
            
            if (!this.viewModel)
                throw 'viewModel must be passed in.';
        },
        
        createLayout: function(page){
            var _s = this;
            var fnCreate = function(createFn, region){
                var w = dojo.hitch(_s, createFn)(page);
                if (w){
                    w.set('region', region);
                    page.addChild(w);
                }
            };
            
            fnCreate(this.createToolbar, 'top');
            fnCreate(this._createMainContent, 'center');
            fnCreate(this.createFooter, 'bottom');
            
            this.onLayoutCreated();
        },
        
        onLayoutCreated: function(){},
        
        createToolbar: function(){
            return new Toolbar({
                viewModel:  this.viewModel
            });
        },
        
        _createMainContent: function(){
            this.mainContent = new _ContentPane({
                controller: this,
                content:    this.createMainContent()
            });
            return this.mainContent;
        },
        
        createSecondaryNavigation: function(node){
            
            var qr = this.viewModel.query({ 
                _type:          'evf.contextNav'
            });
            
            if (qr.length == 0){
                return null;
            }
            
            if (qr.length > 1)
                throw 'multiple context navigation mechanisms are not supported.';
            
            return menuUtils.buildNavigation(qr[0], node, 'secondaryNavigationPopup');
        },
        
        createMainContent: function(){
            throw 'createMainContent is undefined';
        },
        
        createFooter: function(){
            return null;
        },
        
        setPageTitle: function(title){
        	this.mainContent.pageTitleNode.innerHTML = title;
        	dojo.doc.title = title;
        }
    });/*declare*/  
});/*define*/