package com.tuanjian.common.dao;

import java.util.List;
import java.util.Map;

public interface BaseDao<T> {

    T selectOne(Long id);

    List<T> selectList(Map<String, Object> fields);

    List<T> selectByPage(Map<String, Object> fields);

    Long count(Map<String, Object> fields);

    Long insert(T entity);

    Long update(T entity);

    Long delete(Map<String, Object> fields);
}
