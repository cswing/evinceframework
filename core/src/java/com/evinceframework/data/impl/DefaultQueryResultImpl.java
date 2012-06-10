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
package com.evinceframework.data.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.evinceframework.data.QueryParameters;
import com.evinceframework.data.QueryResult;

public class DefaultQueryResultImpl<T> implements QueryResult<T> {

	private QueryParameters parameters;
	
	private Integer page;
	
	private Integer pageSize;
	
	private Integer totalItems;
	
	private Integer totalPages;

	private Integer firstItemIndex;
	
	private Integer lastItemIndex;
	
	private List<T> items;
	
	public DefaultQueryResultImpl(QueryParameters parameters) {
		this.parameters = parameters;
		this.page = parameters.getPage();
		this.pageSize = parameters.getPageSize();
		
		calculateIndices();
	}

	protected void calculateIndices() {
		if (page <= 0)
			page = 1;
		
		if (items == null)
			items = Collections.unmodifiableList(new ArrayList<T>());
		
		if (totalItems == null || totalItems <= 0) {
			firstItemIndex = 0;
			lastItemIndex = 0;
			totalPages = 0;
			totalItems = 0;
			
		} else if (pageSize == null) {
			firstItemIndex = 1;
			lastItemIndex = totalItems;
			totalPages = 1;
			
		} else {
			firstItemIndex = pageSize * (page-1) + 1;
			lastItemIndex = pageSize * (page-1) + pageSize;
			totalPages = totalItems / pageSize;
			if (totalItems % pageSize > 0) {
				totalPages++;
			}
			if (lastItemIndex > totalItems) {
				lastItemIndex = totalItems;
			}
		}
	}
	
	@Override
	public QueryParameters getParameters() {
		return parameters;
	}

	@Override
	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
		calculateIndices();
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		calculateIndices();
	}

	@Override
	public Integer getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
		calculateIndices();
	}

	@Override
	public Integer getTotalPages() {
		return totalPages;
	}

	@Override
	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = Collections.unmodifiableList(items);
	}

	public Integer getFirstItemIndex() {
		return firstItemIndex;
	}

	public Integer getLastItemIndex() {
		return lastItemIndex;
	}
	
}
