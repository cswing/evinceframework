package com.evinceframework.jpa.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.util.StringUtils;

public class SingularQueryParameterDescriptor<T, JAVA_TYPE> {
	private Class<JAVA_TYPE> clazz;
	private String name; // optional
	private SingularAttribute<T, String> attribute = null;
		
	public void setClass(Class<JAVA_TYPE> clazz) {
		this.clazz = clazz;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAttribute(SingularAttribute<T, String> attribute) {
		this.attribute = attribute;
	}

	public ParameterExpression<JAVA_TYPE> addEqualsPredicateToWhereClause(CriteriaBuilder builder, CriteriaQuery<T> criteria, Path<T> path) {
		ParameterExpression<JAVA_TYPE> parameter = createExpression(builder);
		criteria.where(createEqualsPredicate(builder, path, parameter));
		return parameter;
	}
	
	public ParameterExpression<JAVA_TYPE> createExpression(CriteriaBuilder builder) {									
		return StringUtils.hasText(name) ? builder.parameter(clazz, name) : builder.parameter(clazz);					
	}
	
	public Predicate createEqualsPredicate(CriteriaBuilder builder, Path<T> path, ParameterExpression<JAVA_TYPE> parameter) {
		return builder.equal(path.get(attribute), parameter);
	}
}