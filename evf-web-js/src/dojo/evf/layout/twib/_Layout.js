/*
 * Copyright 2013 Craig Swing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 define([
    'dojo/_base/declare',
    'dojo/_base/window',
    'dojo/dom-geometry', 
    'dojo/window', 
    'dijit/_TemplatedMixin', 
    'dijit/Viewport', 
    'evf/ComplexWidget'
], function(declare, win, domGeom, winUtils, Templated, Viewport, ComplexWidget, template){
                
    return declare('evf.layout.twib._Layout', [ComplexWidget, Templated], {
        
        templateString: null,
        
        viewModel: null,

        navBarClass: null,
        
        navBarInnerNode: null, 
        
        navBarNode: null,
        
        navBar: null,
        
        contentNode: null,
        
        buildRendering: function() {
            if(!this.templateString)
                throw this.dojoLang.replace('${_declaredClass} does not provide a template.', this);

            this.inherited(arguments);
        },

        postCreate: function(){
            this.inherited(arguments);
            
            var navBarClass = this.navBarClass;
            if (this.dojoLang.isString(navBarClass)) {
                navBarClass = this.dojoLang.getObject(navBarClass);
            }
            
            if(navBarClass) {
                this.navBar = new navBarClass({viewModel: this.viewModel}, this.navBarNode);
            } else {
                console.warn('Unknown navigation bar: ' + navBarClass);
            }
        },
        
        startup: function() {
            if(this._started){ return; }
            this.inherited(arguments);
            
            if(this.navBar)
                this.navBar.startup();
            
            this.set('pageTitle', win.doc.title);

            this.resize();
            this.own(Viewport.on("resize", this.hitch("resize")));
        },
        
        resize: function() {
            var vp = winUtils.getBox(),
                navHeight = domGeom.getMarginBox(this.navBarInnerNode).h;
            
            this.domStyle.set(this.contentLayoutNode, 'paddingTop', navHeight + 'px');
            this.domStyle.set(this.contentLayoutNode, 'minHeight', (vp.h - navHeight) + 'px');
        },

        _setPageTitleAttr: function(title){

            if(this.titleNode)
                this.domHtml.set(this.titleNode, title);
            win.doc.title = title;
        }
    });
});