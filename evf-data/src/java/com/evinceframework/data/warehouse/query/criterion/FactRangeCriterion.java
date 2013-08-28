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
package com.evinceframework.data.warehouse.query.criterion;

import com.evinceframework.data.warehouse.Fact;

public class FactRangeCriterion<T extends Number> implements Criterion {

	private Fact<T> fact;
	
	private boolean upperBoundInclusive = true;
	
	private T upperBound;
	
	private boolean lowerBoundInclusive = true;
	
	private T lowerBound;

	public FactRangeCriterion(Fact<T> fact) {
		this.fact = fact;
	}

	public boolean isUpperBoundInclusive() {
		return upperBoundInclusive;
	}

	public void setUpperBoundInclusive(boolean upperBoundInclusive) {
		this.upperBoundInclusive = upperBoundInclusive;
	}

	public T getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(T upperBound) {
		this.upperBound = upperBound;
	}

	public boolean isLowerBoundInclusive() {
		return lowerBoundInclusive;
	}

	public void setLowerBoundInclusive(boolean lowerBoundInclusive) {
		this.lowerBoundInclusive = lowerBoundInclusive;
	}

	public T getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(T lowerBound) {
		this.lowerBound = lowerBound;
	}

	public Fact<T> getFact() {
		return fact;
	}
	
}
