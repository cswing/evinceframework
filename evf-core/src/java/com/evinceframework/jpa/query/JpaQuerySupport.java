package com.evinceframework.jpa.query;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;

import com.evinceframework.jpa.SqlDataTypeSupport;

public abstract class JpaQuerySupport<T, ROOT, INIT_CONTEXT> {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private SqlDataTypeSupport sqlDataTypeSupport;
	
	private Class<T> returnClazz;
	
	private Class<ROOT> rootClazz;
	
	protected JpaQuerySupport(Class<T> returnType, Class<ROOT> rootClass) {
		returnClazz = returnType;
		rootClazz = rootClass;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public SqlDataTypeSupport getSqlDataTypeSupport() {
		return sqlDataTypeSupport;
	}

	protected abstract void createCriteria(CriteriaBuilder builder, CriteriaQuery<T> criteria, Root<ROOT> root, INIT_CONTEXT context);
	
	protected CriteriaQuery<T> initializeCriteria(INIT_CONTEXT context) {
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();		
		CriteriaQuery<T> criteria = builder.createQuery(returnClazz);
		Root<ROOT> root = criteria.from(rootClazz);
		
		createCriteria(builder, criteria, root, context);
		
		return criteria;
	}
	
}
