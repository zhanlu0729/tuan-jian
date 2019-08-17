package com.tuanjian.common.service;

import com.github.pagehelper.PageInfo;
import com.tuanjian.common.utils.Page;
import java.util.List;

public interface BaseService<T> {

    T getOne(Long id);

    T getOne(T entity);

    List<T> findList(T entity);

    PageInfo<T> findByPage(Page page, T entity);

    Long count(T entity);

    Long save(T entity);

    Long update(T entity);

    Long delete(Long id);

    Long delete(T entity);

}
