package com.yjy.core.jpa.query;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

/**
 * 用于创建WJCriteriaBuilder
 */
@Component
public class PredicateUtils {
	
	/** 注入 EntityManager **/
	private static EntityManager entityManager = null;
	
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		PredicateUtils.entityManager = entityManager;
	}
	
	public static EntityManager getEntityManager() {
		return entityManager;
	}
	
	/**
	 * 创建WJCriteriaBuilder对象
	 * @param clazz 类实体
	 */
	public static WJCriteriaBuilder createWJCriteriaBuilder(Class<?> clazz) {
		return WJCriteriaBuilder.createWJBuilder(clazz, entityManager);
	}
}
