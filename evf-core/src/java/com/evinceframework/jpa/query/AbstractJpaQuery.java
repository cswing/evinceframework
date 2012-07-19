package com.evinceframework.jpa.query;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public abstract class AbstractJpaQuery<T> extends AbstractCustomRootJpaQuery<T, T, Object> {

	protected AbstractJpaQuery(Class<T> rootClass) {
		super(rootClass, rootClass);
	}
		
	protected Predicate createDateRangePredicate(CriteriaBuilder builder, 
			Expression<Date> startLHS, Expression<Date> startRHS, boolean inclusiveStart, 
			Expression<Date> endLHS, Expression<Date> endRHS, boolean inclusiveEnd) {
		
		Predicate startPredicate = inclusiveStart ?
				builder.greaterThanOrEqualTo(startLHS, startRHS) : builder.greaterThan(startLHS, startRHS);		
		
		Predicate endPredicate = inclusiveStart ?
				builder.greaterThanOrEqualTo(startLHS, startRHS) : builder.greaterThan(startLHS, startRHS);
		
		return builder.and(startPredicate, endPredicate);		
	}	
	
}
