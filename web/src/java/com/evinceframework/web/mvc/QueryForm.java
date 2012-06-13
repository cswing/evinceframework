package com.evinceframework.web.mvc;

import com.evinceframework.data.QueryParameters;
import com.evinceframework.data.impl.DefaultQueryParametersImpl;

public class QueryForm implements QueryParameters {

	private Integer pageSize = DefaultQueryParametersImpl.DEFAULT_PAGE_SIZE;
	
	private Integer page = DefaultQueryParametersImpl.DEFAULT_PAGE;
	
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

}
