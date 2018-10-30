package com.yjy.core.jpa.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.yjy.core.jpa.domain.IdEntity;

/**
 * 步骤二：自定义Repository接口
 * 继承了JpaSpecificationExecutor、CrudRepository
 * 使用时只需要继承接口，不需要实现类，spring自动通过cglib生成实现
 * 添加@NoRepositoryBean标注，这样Spring Data Jpa在启动时就不会去实例化BaseDao这个接口
 * @param <T> 实体类型
 */
@NoRepositoryBean
public interface JpaDao<T extends IdEntity>
		extends CrudRepository<T, Serializable>/* JpaRepository<T, Serializable> */, JpaPredicateExecutor {

}
