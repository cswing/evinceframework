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
import java.util.List;

import com.evinceframework.data.Order;
import com.evinceframework.data.QueryParameters;

public class DefaultQueryParametersImpl implements QueryParameters {

	public static final Integer DEFAULT_PAGE_SIZE = 25;
	
	public static final Integer DEFAULT_PAGE = 1;
	
	private Integer page = DEFAULT_PAGE;
	
	private Integer pageSize = DEFAULT_PAGE_SIZE;

	private List<Order> orders = new ArrayList<Order>(); 
	
	@Override
	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	@Override
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public List<Order> getOrder() {
		return orders;
	}
	
	public void addOrderAscending(String path) {
		orders.add(new OrderImpl(true, path));
	}
	
	public void addOrderDescending(String path) {
		orders.add(new OrderImpl(false, path));
	}
	
	public class OrderImpl implements Order {
		
		private boolean isAscending;
		
		private String field;

		public OrderImpl(boolean isAscending, String field) {
			this.isAscending = isAscending;
			this.field = field;
		}

		@Override
		public boolean isAscending() {
			return isAscending;
		}

		@Override
		public String getSortField() {
			return field;
		}
	}
}
