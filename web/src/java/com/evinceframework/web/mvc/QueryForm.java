package com.evinceframework.web.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.evinceframework.data.Order;
import com.evinceframework.data.QueryParameters;
import com.evinceframework.data.impl.DefaultQueryParametersImpl;

public class QueryForm implements QueryParameters {

	private Integer pageSize = DefaultQueryParametersImpl.DEFAULT_PAGE_SIZE;
	
	private Integer page = DefaultQueryParametersImpl.DEFAULT_PAGE;
	
	private List<QueryFormOrderImpl> orders = new ArrayList<QueryFormOrderImpl>();
	
	@Override
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	@Override
	public List<Order> getOrder() {
		List<Order> order = new ArrayList<Order>();
		for(QueryFormOrderImpl impl : orders) {
			order.add(impl);
		}
		return Collections.unmodifiableList(order);
	}

	public List<QueryFormOrderImpl> getOrders() {
		return orders;
	}

	public void setOrders(List<QueryFormOrderImpl> orders) {
		this.orders = orders;
	}
	
	public static class QueryFormOrderImpl implements Order {
		
		private boolean isAscending;
		
		private String field;

		public boolean isAscending() {
			return isAscending;
		}

		public void setAscending(boolean isAscending) {
			this.isAscending = isAscending;
		}

		public String getSortField() {
			return field;
		}

		public void setSortField(String field) {
			this.field = field;
		}
	}
}
