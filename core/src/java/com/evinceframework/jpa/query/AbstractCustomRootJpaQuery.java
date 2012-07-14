package com.evinceframework.jpa.query;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractCustomRootJpaQuery<T, ROOT, CONTEXT> extends JpaQuerySupport<T, ROOT, CONTEXT> implements InitializingBean {

	protected static final boolean DEFAULT_THROW = true;
		
	private CriteriaQuery<T> criteria;
	
	protected AbstractCustomRootJpaQuery(Class<T> returnType, Class<ROOT> rootClass) {
		super(returnType, rootClass);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {		
		criteria = initializeCriteria(null);
	}
	
	protected T executeSingleResult(TypedQuery<T> query) {		    	
    	return executeSingleResult(query, DEFAULT_THROW);    	
	}
	
	protected TypedQuery<T> createQuery() {		
		return getEntityManager().createQuery(criteria);		
	}	
	
	@Override
	protected void createCriteria(CriteriaBuilder builder, CriteriaQuery<T> criteria, Root<ROOT> root, CONTEXT context) {
		createCriteria(builder, criteria, root);
	}

	protected abstract void createCriteria(CriteriaBuilder builder, CriteriaQuery<T> criteria, Root<ROOT> root);
	
	protected T executeSingleResult(TypedQuery<T> query, boolean throwWhenNotFound) {
		if (throwWhenNotFound)
			return query.getSingleResult();		
		else
			try {
				return query.getSingleResult();
			} catch (NoResultException nre) {
				return null;
			}
	}

}
