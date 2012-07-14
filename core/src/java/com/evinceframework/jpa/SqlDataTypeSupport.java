package com.evinceframework.jpa;

import java.util.Date;

public class SqlDataTypeSupport {

	private Date minimumDate;
	
	private Date maximumDate;

	public Date getMinimumDate() {
		return minimumDate;
	}

	public void setMinimumDate(Date minimumDate) {
		this.minimumDate = minimumDate;
	}

	public Date getMaximumDate() {
		return maximumDate;
	}

	public void setMaximumDate(Date maximumDate) {
		this.maximumDate = maximumDate;
	}
	
}
