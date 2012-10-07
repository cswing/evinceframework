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
 define("evf/data/Grid", [
  "dojo", "dijit", "dojo/dom-construct", "dojo/dom-geometry",
  "dijit/_Widget", "dijit/_TemplatedMixin"
], function(dojo, dijit, domConstruct, domGeometry, Widget, Template) {

return dojo.declare("evf.data.Grid", [Widget, Template], {

	items:		null,
	
	structure:      null,
  
	baseClass:      'dataGrid',
  
	templateString: dojo.cache('evf.data', 'templates/Grid.html'),
  
  postCreate: function() {
    this.inherited(arguments);
    dojo.addClass(this.domNode, this.baseClass);
  },
  
  _forceResize: function(){
	  this.resize(domGeometry.getMarginBox(this.domNode));
  },
  
  resize: function(changeSize) {
    this.inherited(arguments);
    
    if(!changeSize)
      return;
    
    domGeometry.setMarginBox(this.domNode, changeSize);
    
    dojo.style(this.innerContainer, 'height', dojo.replace('{0}px', [ 
      changeSize.h - domGeometry.getMarginBox(this.headerRowNode).h]));
    
    if (!this._started)
    	return;
    
    // calculate column widths 
    var tableWidth = changeSize.w,
      totalDefinedWidth = 0,
      totalMinimumDynamicWidth = 0,
      defaultMinimumWidth = 100,
      scrollWidth = 0, //64,//24,
      widths = [],
      dynamicWidthIndices = [],
      availableTableWidth = tableWidth - scrollWidth;
       
    dojo.forEach(this.structure, function (cellDef, idx) {   
      
      if(cellDef.width) {
        widths.push(cellDef.width);  
        totalDefinedWidth += cellDef.width;
        
      } else {
        var w = cellDef.minimumWidth || defaultMinimumWidth;
        widths.push(w);
        dynamicWidthIndices.push(idx);
        totalMinimumDynamicWidth += w;
      }
    });
    
    // a spacer is needed if the defined widths do not take up the entire table width
    // and there are no dynamic cells that would.
    var isSpacerNeeded = totalDefinedWidth < availableTableWidth && dynamicWidthIndices.length == 0;
    
    var remainingWidthToAllocate = availableTableWidth - (totalDefinedWidth + totalMinimumDynamicWidth);
       
    // determine if the cells will take up more room than the  
    var isWiderThanTable = remainingWidthToAllocate < 0; 
    
    var additionalDynamicWidth = 0;
    if (dynamicWidthIndices.length > 0) {
      additionalDynamicWidth = remainingWidthToAllocate / dynamicWidthIndices.length;
    }
    
    dojo.forEach(this.structure, function (cellDef, idx) {
      var isDynamic = !cellDef.width;
      var width = widths[idx];
      
      if (isDynamic && additionalDynamicWidth > 0) {
        width += additionalDynamicWidth;
      }
       
      dojo.style(this._thsByCellDef[idx], 'width', width + 'px');
      dojo.forEach(this._tdsByCellDef[idx], function(cell) {
        dojo.style(cell, 'width', width + 'px');  
      });
        
    }, this);
  },
  
  startup: function() {
    this.inherited(arguments);
    this._buildGrid();
    this._forceResize();
  },
  
  _buildGrid: function() {
    
    this._thsByCellDef = {};
    this._tdsByCellDef = {};
    
    dojo.forEach(this.structure, function (cellDef, colIdx) {
      this._buildHeaderCell(this.headerRowNode, cellDef, colIdx);
    }, this);
    this._spacerHeadCell = domConstruct.create('td', {'class': 'columnHeader'}, this.headerRowNode);
    dojo.addClass(this._spacerHeadCell, 'columnHeaderPad');
    dojo.style(this._spacerHeadCell, 'width', '16px');
    dojo.attr(this.parentBodyCell, 'colSpan', this.structure.length + 1);
    
    this._buildData();
  },
  
  _buildHeaderCell: function(tr, cellDef, colIdx) {
    var th = domConstruct.create('td', {'class': 'columnHeader'}, tr);
    this._thsByCellDef[colIdx] = th;
    if(cellDef.sortable) {
    	dojo.addClass(th, 'sortable');
    }
    
    var text = domConstruct.create('div', {'class' : 'dijitInline'}, th);    
    domConstruct.create('div', {'class' : 'dijitInline sortIcon'}, th);
    
    if (cellDef.renderHeader && dojo.isFunction(cellDef.renderHeader)) {
      cellDef.renderHeader(th, text, cellDef, colIdx);
    } else {
    	text.innerHTML = cellDef.caption;  
    }
  },
  
  _buildData: function() {
	
	dojo.forEach(this._connects, this.disconnect, this);
	domConstruct.empty(this.bodyNode);
	
	// update column header (sorting)
	var hasOrder = this.metadata && this.metadata.parameters.order && this.metadata.parameters.order.length > 0;		
	var orderField = hasOrder ? this.metadata.parameters.order[0].sortField : '';
	var isAsc = hasOrder ? this.metadata.parameters.order[0].ascending : false;
		
	dojo.forEach(this.structure, function (cellDef, colIdx) {
		
		if (!cellDef.sortable)
			return;
		
		var th = this._thsByCellDef[colIdx];
		
		var isPrimarySort = false;
		if (hasOrder) {
			if (cellDef.customSort) {
				isPrimarySort = cellDef.customSort[0] == orderField;
			} else {
				isPrimarySort = cellDef.field == orderField;
			}
		}
		
		var ascOrderArray = [], dscOrderArray = [];
		dojo.forEach(cellDef.customSort ? cellDef.customSort : [cellDef.field], function(field) {
			ascOrderArray.push({ ascending: true, sortField: field });
			dscOrderArray.push({ ascending: false, sortField: field });
		});

		if(isPrimarySort) {
			dojo.addClass(th, 'primarySort');
			if(isAsc) {
				dojo.addClass(th, 'asc');
				dojo.removeClass(th, 'desc');
				this.connect(th, 'onclick', function() {
					this.onSort(dscOrderArray);
				});
			} else {
				dojo.addClass(th, 'desc');
				dojo.removeClass(th, 'asc');
				this.connect(th, 'onclick', function() {
					this.onSort(ascOrderArray);
				});
			}
		} else {
			dojo.removeClass(th, 'primarySort');
			dojo.removeClass(th, 'asc');
			dojo.removeClass(th, 'desc');
			this.connect(th, 'onclick', function() {
				this.onSort(ascOrderArray);
			});
		}
    }, this);
	
	dojo.forEach(this.items || [], this._buildRow, this);  
  },
  
  _buildRow: function(data, idx) {
    var tr = domConstruct.create('tr', {'class': 'dataRow'}, this.bodyNode);
    dojo.addClass(tr, idx % 2 == 0 ? 'odd' : 'even');
    
    dojo.forEach(this.structure, function(cellDef, colIdx) {
      this._buildDataCell(tr, data, idx, colIdx, cellDef);
    }, this);
  },
  
  _buildDataCell: function(tr, data, rowIdx, colIdx, cellDef) {
    var td = domConstruct.create('td', {'class': 'columnData'}, tr);
    
    if (!this._tdsByCellDef[colIdx]) {
      this._tdsByCellDef[colIdx] = [];  
    }
    this._tdsByCellDef[colIdx].push(td);
    
    if (cellDef.renderCell && dojo.isFunction(cellDef.renderCell)) {
      cellDef.renderCell(td, data, rowIdx, colIdx, cellDef);
    } else {
      td.innerHTML = data[cellDef.field];  
    }
  },
  
  _setItemsAttr: function(items){
	  this.items = items || [];
	  if (this._started) {
		  this._buildData();
		  this._forceResize();
	  }
  },
  
  _setMetadataAttr: function(meta) {
	  this.metadata = meta;
	  this.set('items', meta.items);
  },
  
  onSort: function(/*Array*/ sortOrder) {}
  
});

});