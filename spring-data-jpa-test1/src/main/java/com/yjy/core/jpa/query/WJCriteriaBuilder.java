package com.yjy.core.jpa.query;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.StringUtils;

@SuppressWarnings("unchecked")
public class WJCriteriaBuilder {
	
	/**
	 * 属性类型转换
	 */
	private static final ConversionService conversionService = new DefaultConversionService();
	
	/**
	 * builder
	 */
	private CriteriaBuilder criteriaBuilder;

	/**
	 * 查询对象
	 */
	private CriteriaQuery<?> criteriaQuery;

	/** 查询条件列表 */
	private Root<?> root;
	
	public CriteriaBuilder getCriteriaBuilder() {
		return criteriaBuilder;
	}
	public CriteriaQuery<?> getCriteriaQuery() {
		return criteriaQuery;
	}
	public Root<?> getRoot() {
		return root;
	}
	
	private WJCriteriaBuilder(Class<?> clazz, EntityManager entityManager) {
		criteriaBuilder = entityManager.getCriteriaBuilder();
		criteriaQuery = criteriaBuilder.createQuery(clazz);
		root = criteriaQuery.from(clazz);
	}
	
	public static WJCriteriaBuilder createWJBuilder(Class<?> clazz, EntityManager entityManager) {
		return new WJCriteriaBuilder(clazz, entityManager);
	}
	
	/**
	 * 等于
	 */
	public Predicate equal(String propertyName, Object object, JoinType... joinType) {
		Expression<?> expression = getExpression(propertyName, joinType);
		return criteriaBuilder.equal(expression, conversionObjectToJavaType(object, expression.getJavaType()));
	}
	
	/**
	 * 不等于
	 */
	public Predicate notEqual(String propertyName, Object object, JoinType... joinType) {
		Expression<?> expression = getExpression(propertyName, joinType);
		return criteriaBuilder.notEqual(expression, conversionObjectToJavaType(object, expression.getJavaType()));
	}
	
	/**
	 * 属性为空
	 */
	public Predicate isNull(String propertyName, Object object, JoinType... joinType) {
		Expression<?> expression = getExpression(propertyName, joinType);
		return criteriaBuilder.isNull(expression);
	}

	/**
	 * 属性不为空
	 */
	public Predicate isNotNull(String propertyName, Object object, JoinType... joinType) {
		Expression<?> expression = getExpression(propertyName, joinType);
		return criteriaBuilder.isNotNull(expression);
	}
	
	/**
	 * 全匹配(%object%)
	 */
	public Predicate likeAll(String propertyName, Object object, JoinType... joinType) {
		Expression<String> expression = (Expression<String>) getExpression(propertyName, joinType);
		return criteriaBuilder.like(expression, "%" + object + "%");
	}

	/**
	 * 左匹配(object%)
	 */
	public Predicate likeLeft(String propertyName, Object object, JoinType... joinType) {
		Expression<String> expression = (Expression<String>) getExpression(propertyName, joinType);
		return criteriaBuilder.like(expression, object + "%");
	}

	/**
	 * 右匹配(object%)
	 */
	public Predicate likeRight(String propertyName, Object object, JoinType... joinType) {
		Expression<String> expression = (Expression<String>) getExpression(propertyName, joinType);
		return criteriaBuilder.like(expression, "%" + object);
	}
	
	/**
	 * 大于某个值
	 */
	@SuppressWarnings("rawtypes")
	public Predicate greaterThan(String propertyName, Object object, JoinType... joinType) {
		Expression<?> expression = getExpression(propertyName, joinType);
		Class<?> javaType = expression.getJavaType();
		object = conversionObjectToJavaType(object, javaType);
		if (javaType.getSuperclass() == Number.class) {
			// 数值
			return criteriaBuilder.gt((Expression<? extends Number>) expression, (Number) object);
		} else {
			return criteriaBuilder.greaterThan((Expression<? extends Comparable>) expression, (Comparable) object);
		}
	}
	
	/**
	 * 大于或等于某个值
	 */
	@SuppressWarnings("rawtypes")
	public Predicate greaterThanOrEqualTo(String propertyName, Object object, JoinType... joinType) {
		Expression<?> expression = getExpression(propertyName, joinType);
		Class<?> javaType = expression.getJavaType();
		object = conversionObjectToJavaType(object, javaType);
		if (javaType.getSuperclass() == Number.class) {
			return criteriaBuilder.ge((Expression<? extends Number>) expression, (Number) object);
		} else {
			return criteriaBuilder.greaterThanOrEqualTo((Expression<? extends Comparable>) expression,
					(Comparable) object);
		}
	}
	
	/**
	 * 小于某个值
	 */
	@SuppressWarnings("rawtypes")
	public Predicate lessThan(String propertyName, Object object, JoinType... joinType) {
		Expression<?> expression = getExpression(propertyName, joinType);
		Class<?> javaType = expression.getJavaType();
		object = conversionObjectToJavaType(object, javaType);
		
		if (javaType.getSuperclass() == Number.class) {
			// 数值
			return criteriaBuilder.lt((Expression<? extends Number>) expression, (Number) object);
		} else {
			return criteriaBuilder.lessThan((Expression<? extends Comparable>) expression, (Comparable) object);
		}
	}
	
	/**
	 * 小于或等于某个值
	 */
	@SuppressWarnings("rawtypes")
	public Predicate lessThanOrEqualTo(String propertyName, Object object, JoinType... joinType) {
		Expression<?> expression = getExpression(propertyName, joinType);
		Class<?> javaType = expression.getJavaType();
		object = conversionObjectToJavaType(object, javaType);
		// 数值
		if (javaType.getSuperclass() == Number.class) {
			return criteriaBuilder.le((Expression<? extends Number>) expression, (Number) object);
		} else {
			return criteriaBuilder.lessThanOrEqualTo((Expression<? extends Comparable>) expression,
					(Comparable) object);
		}
	}
	
	/**
	 * in
	 * @param object 格式：str1,str2;Collection<String>, String[]
	 */
	@SuppressWarnings("rawtypes")
	public Predicate in(String propertyName, Object object, JoinType... joinType) {
		Expression<?> expression = getExpression(propertyName, joinType);
		String[] values = null;
		if (object instanceof String) {
			values = StringUtils.split(object.toString(), ",");
		} else if (object instanceof String[]) {
			values = (String[]) object;
		} else if (object instanceof Collection) {
			values = ((Collection<String>) object).toArray(new String[] {});
		}
		if (values == null) {
			throw new IllegalArgumentException("criteria builder in sql error"); 
		}
		
		if (values.length == 1) {
			return criteriaBuilder.equal(expression, values[0]);
		} else {
			In in = criteriaBuilder.in(expression);
			for (String string : values) {
				in.value(string);
			}
			return in;
		}
	}
	
	/**
	 * or关联
	 */
	public Predicate or(Predicate[] predicates) {
		return criteriaBuilder.or(predicates);
	}
	
	/**
	 * or关联
	 */
	public Predicate or(Predicate leftPredicate, Predicate rightPredicate) {
		return criteriaBuilder.or(leftPredicate, rightPredicate);
	}
	
	/**
	 * and关联
	 */
	public Predicate and(Predicate... predicates) {
		return criteriaBuilder.and(predicates);
	}

	/**
	 * 类型转换
	 */
	private Object conversionObjectToJavaType(Object source, Class<?> clazz) {
		return conversionService.convert(source, clazz);
	}
	
	private Expression<?> getExpression(String propertyName, JoinType... joinType) {
		String[] names = StringUtils.split(propertyName, ".");
		Path<?> expression = root.get(names[0]);
		
		Class<?> javaType = expression.getJavaType();
		if (Collection.class.isAssignableFrom(javaType)) {
			expression = root.join(names[0], joinType == null ? JoinType.INNER : joinType[0]);
		}
		// nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
		for (int i = 1; i < names.length; i++) {
			expression = expression.get(names[i]);
		}
		return expression;
	}
}
