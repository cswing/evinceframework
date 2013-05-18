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
 	'dojo/_base/array',
 	'dojo/_base/declare',
 	'dijit/_CssStateMixin',
	'dijit/_TemplatedMixin',
	'evf/ComplexWidget',
	'dojo/i18n!./nls/pager',
	'dojo/text!./templates/PagingWidget.html'
], function(array, declare, CssStateMixin, Templated, ComplexWidget, i18n, template) {

	// { metadata: this.metadata, requestedPage: page}

	return declare('evf.data.PagingWidget', [ComplexWidget, Templated, CssStateMixin], {
		// summary:

		templateString: template,

		navigateEvent: 'navigate',

		dataSource: null,
		// summary: __PageableDataSource
		//		The dataSource that describes the set of data that this widget is responsible for paging.

		_dynamicLinks: null,

		navSignals: null,

		postMixInProperties: function(){
			this.inherited(arguments);
			this.i18n = i18n;

			this.cssStateNodes = {
				'previousNode' : 'evfPageNumbers',
				'pageNodeFirst' : 'evfPageNumbers',
				'pageNodeIdx2' : 'evfPageNumbers',
				'pageNodeIdx3' : 'evfPageNumbers',
				'pageNodeIdx4' : 'evfPageNumbers',
				'pageNodeIdx5' : 'evfPageNumbers',
				'pageNodeLast' : 'evfPageNumbers',
				'nextNode' : 'evfPageNumbers'
			};
		},

		postCreate: function() {
			this.inherited(arguments);

		},

		startup: function() {
			if(this._started) return;
			this.inherited(arguments);

			this.set('dataSource', this.dataSource);
		},

		_setDataSourceAttr: function(dataSource){
			this.dataSource = dataSource;
			if(!this._started) return;
			

			if(this.navSignals) {
				array.forEach(this.navSignals, function(signal){ signal.remove();  });
			}
			this.navSignals = [];

			if(this._dynamicLinks) {
				array.forEach(this._dynamicLinks, this.domConstruct.destroy);
			}
			this._dynamicLinks = [];

			var display = this.dataSource.totalPages <= 1;
			this.domStyle.set(this.domNode, 'display', display ? 'none' : '');
			if(display) return;

			// calculate the inner numbers needed to display
			var startLimit = 2;
			var endLimit = this.dataSource.totalPages - 1;
			var increment = 2;

			// Determine the inner pages
			var start = Math.max(startLimit, this.dataSource.page - increment);
			var end = Math.min(endLimit, this.dataSource.page + increment);
			if(end < start) end = start;
			if (end - start < 4) {
				var needed = 4 - (end - start);
				if (start == startLimit && end < endLimit) {
					end = Math.min(end + needed, endLimit);
				}

				if (end == endLimit && start > startLimit) {
					start = Math.max(startLimit, end - needed);
				}
			}

			// first page, previous page: wire up and display rules
			var fnWireLink = this.hitch(function(a, page) {
				this.navSignals.push(
					this.listen(a, 'click', function() { 
						this.emit(this.navigateEvent, { dataSource: this.dataSource, requestedPage: page}); 
					})
				);
			});
			var fnSetDisabledClass = this.hitch(function(node, isDisabled) {
				var fn = isDisabled ? this.domClass.add : this.domClass.remove;
				fn(node, 'evfPageNumberDisabled');
			});

			if (this.dataSource.page == 1) {
				this.domClass.add(this.pageNodeFirst, 'evfPageNumberSelected');
			} else {
				this.domClass.remove(this.pageNodeFirst, 'evfPageNumberSelected');
				fnWireLink(this.pageNodeFirst.children[0], 1);
				fnWireLink(this.previousNode.children[0], this.dataSource.page - 1);
			}
			fnSetDisabledClass(this.previousNode, this.dataSource.page == 1);
	  
	  		// last page, previous page: wire up and display rules
			if (this.dataSource.page == this.dataSource.totalPages) {
		  		this.domClass.add(this.pageNodeLast, 'evfPageNumberSelected');
	  		} else {
				this.domClass.remove(this.pageNodeLast, 'evfPageNumberSelected');
				fnWireLink(this.pageNodeLast.children[0], this.dataSource.totalPages);
				fnWireLink(this.nextNode.children[0], this.dataSource.page + 1);
			}
	  		this.domHtml.set(this.pageNodeLast.children[0], this.dataSource.totalPages.toString());
	  		fnSetDisabledClass(this.nextNode, this.dataSource.page == this.dataSource.totalPages);
    
    		// inner pages: wireup and display rules
    		this.domStyle.set(this.dotsNodeFirst, 'display', start <= startLimit ? 'none' : '');
    		this.domStyle.set(this.dotsNodeLast, 'display', end >= endLimit ? 'none' : '');
			for (var i=start, j=2; j<=5; i++, j++) {
				
				var node = this['pageNodeIdx' + i];
				if(node) {
					if(i<=end) {
						
						var a = node.children[0];

						// reset the node
						this.domStyle.set(node, 'display', '');
						this.domClass.remove(node, 'evfPageNumberSelected');

						this.domHtml.set(a, i.toString());

						if (this.dataSource.page == i) {
							this.domClass.add(node, 'evfPageNumberSelected');
						} else {
							fnWireLink(a, i);
						}

					} else {
						// Link not used, hide
						this.domStyle.set(node, 'display', 'none');
					}
				}
			}
		} // _setDataSourceAttr

	});

});