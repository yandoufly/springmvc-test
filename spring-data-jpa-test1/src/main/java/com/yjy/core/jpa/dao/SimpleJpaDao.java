package com.yjy.core.jpa.dao;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.yjy.core.jpa.domain.IdEntity;
import com.yjy.core.jpa.query.WJCriteriaBuilder;


/**步骤三：实现BaseRepository
 * 继承SimpleJpaRepository类，使其拥有Jpa Repository的基本方法
 */
@Transactional
@SuppressWarnings("unchecked")
public class SimpleJpaDao<T extends IdEntity> extends SimpleJpaRepository<T, Serializable> implements JpaDao<T> {

    private final EntityManager entityManager;
    
	/**
	 * 实现第二个构造函数，拿到domainClass和EntityManager两个对象
	 * 知道某个Repository是否支持某个领域对象的类型，因此在实现构造函数时我们将domainClass的信息保留下来
	 */
    public SimpleJpaDao(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    public SimpleJpaDao(JpaEntityInformation<T, Serializable> information, EntityManager entityManager){
    	super(information, entityManager);
    	this.entityManager = entityManager;
    }
    
	@Override
	public long count(WJCriteriaBuilder builder, Predicate predicate) {
		CriteriaBuilder criteriaBuilder = builder.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		query.where(predicate);
		return entityManager.createQuery(query).getSingleResult();
	}

	@Override
	public T findOne(WJCriteriaBuilder builder, Predicate predicate) {
		CriteriaQuery<T> query = (CriteriaQuery<T>) builder.getCriteriaQuery();
		query.where(predicate);
		return entityManager.createQuery(query).getSingleResult();
	}

	@Override
	public List<T> findAll(WJCriteriaBuilder builder, Predicate predicate) {
		TypedQuery<T> typedQuery = getPreTypedQuery(builder, predicate, (Sort)null);
		return typedQuery.getResultList();
	}

	@Override
	public List<T> findAll(WJCriteriaBuilder builder, Predicate predicate, Sort sort) {
		TypedQuery<T> typedQuery = getPreTypedQuery(builder, predicate, sort);
		return typedQuery.getResultList();
	}

	@Override
	public Page<T> findAll(WJCriteriaBuilder builder, Predicate predicate, Pageable pageable) {
		TypedQuery<T> query = getPreTypedQuery(builder, predicate, pageable);
		return pageable == null ? new PageImpl<T>(query.getResultList()) : readPage(query, pageable, predicate);
	}
	
	protected TypedQuery<T> getPreTypedQuery(WJCriteriaBuilder builder, Predicate predicate, Pageable pageable) {
		Sort sort = pageable == null ? null : pageable.getSort();
		return getPreTypedQuery(builder, predicate, sort);
	}

	private TypedQuery<T> getPreTypedQuery(WJCriteriaBuilder builder, Predicate predicate, Sort sort) {
		CriteriaBuilder criteriaBuilder = builder.getCriteriaBuilder();
		CriteriaQuery<T> query = (CriteriaQuery<T>) builder.getCriteriaQuery();
		Root<T> root = (Root<T>) builder.getRoot();
		
		query.select(root); //from
		if (predicate != null) {
			query.where(predicate); //where
		}
		if (sort != null) {
			query.orderBy(QueryUtils.toOrders(sort, root, criteriaBuilder)); //order
		}
		
		return entityManager.createQuery(query);
	}
	
	protected Page<T> readPage(TypedQuery<T> query, Pageable pageable, Predicate predicate) {

		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		Long total = executeCountQuery(getCountQuery(predicate));
		List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.<T> emptyList();

		return new PageImpl<T>(content, pageable, total);
	}
	
	private static Long executeCountQuery(TypedQuery<Long> query) {

		List<Long> totals = query.getResultList();
		Long total = 0L;

		for (Long element : totals) {
			total += element == null ? 0 : element;
		}

		return total;
	}
	
	protected TypedQuery<Long> getCountQuery(Predicate predicate) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		Root<T> root = query.from(getDomainClass());

		if (predicate != null) {
			query.where(predicate);
		}

		if (query.isDistinct()) {
			query.select(builder.countDistinct(root));
		} else {
			query.select(builder.count(root));
		}

		return entityManager.createQuery(query);
	}
}
