/*
 * Copyright 2013 Craig Swing.
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
	'dojo/_base/declare', 
	'dijit/_TemplatedMixin', 
	'evf/ComplexWidget', 
	'./PagingWidget',
	'dojo/text!./templates/PageableContent.html'
], function(declare, Templated, ComplexWidget, PagingWidget, template){

	/*=====
	var __PageableDataSource = declare('evf.data.PageableDataSource', [], {
		// summary:
		//		A data source that contains metadata about a larger set of data and
		//		the data for the current page.
		//
		// page: int
		//		the current page of data that is currently loaded.
		// pageSize: int
		//		the number of items that are displayed as part of the current page.
		// totalItems: int 
		//		(optional) the total number of items that make up the set of data.
		// totalPages: int
		//		(optional) the total number of pages that it would take to display 
		//		the entire set of data.
		// data: Array
		//		the data for the current page.
	});
	=====*/

	/*=====
    var __PageableContentService = declare('evf.data.PageableContentService', {
        // summary:
        //      
        // loadDataSource
        // navigate: function({ metadata: this.metadata, requestedPage: page})
        //      
        
    });
    =====*/

	return declare('evf.data.PageableContent', [ComplexWidget, Templated], {
                    
        templateString: template,

        listId: null,

        contentClass: null,

        contentProps: null,

        service: null,
        // summary: __PageableContentService
        //      A service that is repsonsible for retrieving content.

		dataSource: null,
		// summary: __PageableDataSource
		//		The dataSource that describes the set of data that this widget is responsible for paging.

        postMixInProperties: function(){
        	this.inherited(arguments);
            this.dataSource = this.service.loadDataSource(this.listId);
        },

        postCreate: function(){
        	this.inherited(arguments);

        	this.pagingWidget = this.constructWidget(PagingWidget, {
        		dataSource: this.dataSource 
        	}, this.pagingNode);

            if(!this.contentClass) {
                console.warn('Unknown content class for: ' + this.listId);
            }
        },

        startup: function() {
            if(this.started) return;
            this.inherited(arguments);

            if(!this.service) throw 'Unknown service';

            this.listen(this.pagingWidget, this.pagingWidget.navigateEvent, '_onNavigate');

            this.set('dataSource', this.dataSource);
        },

        _setDataSourceAttr: function(dataSource) {
            this.dataSource = dataSource;
            if(!this._started) return;

            if(this.contentWidget) {
                this.contentWidget.destroyRecursive();
            }

            if(this.contentClass) {
                var props = this.dojoLang.mixin({}, this.contentProps||{});
                props.dataSource = dataSource;
                this.contentWidget = this.constructWidget(
                    this.contentClass, props, this.domConstruct.create('div', {}, this.contentNode));
            }

            this.pagingWidget.set('dataSource', dataSource);
        },

        _onNavigate: function(data) {
            var newData = this.service.navigate(data);
            if(newData) {
                this.set('dataSource', newData);
            }
        }
    });

});