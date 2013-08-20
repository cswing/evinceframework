/*
 * Copyright 2013 the original author or authors.
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
package com.evinceframework.data.warehouse.query;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DrillPathData<T> {

	private DrillPathEntry<?> entry;
	
	private DrillPathData<T> parent;
	
	private List<DrillPathData<T>> children = new LinkedList<DrillPathData<T>>();
	
	private String key;
	
	private T value;

	public DrillPathData(DrillPathData<T> parent, DrillPathEntry<?> entry, String key) {
		this.entry = entry;
		this.parent = parent;
		this.parent.addChild(this);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public DrillPathEntry<?> getEntry() {
		return entry;
	}

	public String getKey() {
		return key;
	}

	public DrillPathData<T> getParent() {
		return parent;
	}

	public List<DrillPathData<T>> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	private void addChild(DrillPathData<T> child) {
		this.children.add(child);
	}
}
