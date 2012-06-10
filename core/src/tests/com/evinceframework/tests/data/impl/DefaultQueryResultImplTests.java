package com.evinceframework.tests.data.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.evinceframework.data.impl.DefaultQueryParametersImpl;
import com.evinceframework.data.impl.DefaultQueryResultImpl;

public class DefaultQueryResultImplTests extends TestCase {

	public void testDefaults() {
		
		DefaultQueryParametersImpl params = new DefaultQueryParametersImpl();
		DefaultQueryResultImpl<Object> result = new DefaultQueryResultImpl<Object>(params);
		
		Object obj = new Object();
		List<Object> items = new ArrayList<Object>();
		for(int i=0; i<25; i++) {
			items.add(obj);
		}
		result.setItems(items);
		result.setTotalItems(75);
		
		assertEquals(new Integer(1), result.getPage());
		assertEquals(new Integer(25), result.getPageSize());
		assertEquals(new Integer(1), result.getFirstItemIndex());
		assertEquals(new Integer(25), result.getLastItemIndex());
		assertEquals(new Integer(75), result.getTotalItems());
		assertEquals(new Integer(3), result.getTotalPages());
	}
	
	public void testSecondPage() {
		
		DefaultQueryParametersImpl params = new DefaultQueryParametersImpl();
		params.setPage(2);
		DefaultQueryResultImpl<Object> result = new DefaultQueryResultImpl<Object>(params);
		
		Object obj = new Object();
		List<Object> items = new ArrayList<Object>();
		for(int i=0; i<25; i++) {
			items.add(obj);
		}
		result.setItems(items);
		result.setTotalItems(75);
		
		assertEquals(new Integer(2), result.getPage());
		assertEquals(new Integer(25), result.getPageSize());
		assertEquals(new Integer(26), result.getFirstItemIndex());
		assertEquals(new Integer(50), result.getLastItemIndex());
		assertEquals(new Integer(75), result.getTotalItems());
		assertEquals(new Integer(3), result.getTotalPages());
	}
	
	public void testNegativePage() {

		DefaultQueryParametersImpl params = new DefaultQueryParametersImpl();
		params.setPage(-1);
		DefaultQueryResultImpl<Object> result = new DefaultQueryResultImpl<Object>(params);
		
		assertEquals(new Integer(1), result.getPage());
	}
	
	public void testNoPageSize() {
		
		DefaultQueryParametersImpl params = new DefaultQueryParametersImpl();
		params.setPage(1);
		params.setPageSize(null);
		DefaultQueryResultImpl<Object> result = new DefaultQueryResultImpl<Object>(params);
		
		Object obj = new Object();
		List<Object> items = new ArrayList<Object>();
		for(int i=0; i<75; i++) {
			items.add(obj);
		}
		result.setItems(items);
		result.setTotalItems(75);
		
		assertEquals(new Integer(1), result.getPage());
		assertEquals(null, result.getPageSize());
		assertEquals(new Integer(1), result.getFirstItemIndex());
		assertEquals(new Integer(75), result.getLastItemIndex());
		assertEquals(new Integer(75), result.getTotalItems());
		assertEquals(new Integer(1), result.getTotalPages());
	}
	
	public void testPartialLastPage() {
		DefaultQueryParametersImpl params = new DefaultQueryParametersImpl();
		params.setPage(3);
		params.setPageSize(25);
		DefaultQueryResultImpl<Object> result = new DefaultQueryResultImpl<Object>(params);
		
		Object obj = new Object();
		List<Object> items = new ArrayList<Object>();
		for(int i=0; i<25; i++) {
			items.add(obj);
		}
		result.setItems(items);
		result.setTotalItems(65);
		
		assertEquals(new Integer(3), result.getPage());
		assertEquals(new Integer(25), result.getPageSize());
		assertEquals(new Integer(51), result.getFirstItemIndex());
		assertEquals(new Integer(65), result.getLastItemIndex());
		assertEquals(new Integer(65), result.getTotalItems());
		assertEquals(new Integer(3), result.getTotalPages());
	}
	
	public void testPageOutOfBounds() {
		DefaultQueryParametersImpl params = new DefaultQueryParametersImpl();
		params.setPage(5);
		params.setPageSize(25);
		DefaultQueryResultImpl<Object> result = new DefaultQueryResultImpl<Object>(params);
		
		Object obj = new Object();
		List<Object> items = new ArrayList<Object>();
		for(int i=0; i<25; i++) {
			items.add(obj);
		}
		result.setItems(items);
		result.setTotalItems(65);
		
		assertEquals(new Integer(3), result.getPage());
		assertEquals(new Integer(25), result.getPageSize());
		assertEquals(new Integer(51), result.getFirstItemIndex());
		assertEquals(new Integer(65), result.getLastItemIndex());
		assertEquals(new Integer(65), result.getTotalItems());
		assertEquals(new Integer(3), result.getTotalPages());
	}
}
