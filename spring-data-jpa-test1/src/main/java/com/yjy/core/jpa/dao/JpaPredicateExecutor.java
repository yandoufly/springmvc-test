package com.yjy.core.jpa.dao;

import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.yjy.core.jpa.query.WJCriteriaBuilder;

/**
 * Predicate相关的查询接口
 */
public interface JpaPredicateExecutor<T> {

	public long count(WJCriteriaBuilder builder, Predicate predicate);

	public T findOne(WJCriteriaBuilder builder, Predicate predicate);

	public List<T> findAll(WJCriteriaBuilder builder, Predicate predicate);

	public List<T> findAll(WJCriteriaBuilder builder, Predicate predicate, Sort sort);

	public Page<T> findAll(WJCriteriaBuilder builder, Predicate predicate, Pageable pageable);
}
