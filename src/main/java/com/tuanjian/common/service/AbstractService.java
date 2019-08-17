package com.tuanjian.common.service;

import com.banksteel.bone.biz.base.dao.BaseDao;
import com.banksteel.bone.biz.base.exception.BusinessException;
import com.banksteel.bone.biz.base.utils.CommonUtils;
import com.banksteel.bone.biz.base.utils.Page;
import com.banksteel.bone.cloud.shared.model.BaseModel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 通用Service接口抽象层
 *
 * @author 杨新伦
 * @date 2019-03-12
 */
@Slf4j
public abstract class AbstractService<T extends BaseModel> implements BaseService<T> {

    @Autowired
    private ApplicationContext applicationContext;
    protected BaseDao<T> baseDao;

    private static final String SQL_WHERE_DESC = "【SQL查询】查询条件：where={}";

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void initBaseDao() {
        // 获取当前new的对象的泛型的父类类型
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取第一个类型参数的真实类型
        Class<T> modelClass = (Class<T>) pt.getActualTypeArguments()[0];
        baseDao = (BaseDao<T>) applicationContext
            .getBean(CommonUtils.getDefaultBeanName(modelClass.getSimpleName()) + "Dao");
        if (baseDao == null) {
            log.error("【baseDao】为空：{}", modelClass);
        }
    }

    @Override
    public T getOne(Long id) {
        if (checkNull(id)) {
            return null;
        }
        T entity = baseDao.selectOne(id);
        if (entity == null) {
            log.info("【SQL查询】单条记录查询结果：id={}", id);
        }
        return entity;
    }

    @Override
    public T getOne(T entity) throws BusinessException {
        if (checkNull(entity)) {
            return null;
        }
        Map<String, Object> whereMap = CommonUtils.getFieldVals(entity);
        this.checkWhereMap(whereMap);
        log.info(SQL_WHERE_DESC, whereMap);
        this.setFieldExt(whereMap, entity);

        List<T> ts = baseDao.selectList(whereMap);
        if (CollectionUtils.isEmpty(ts)) {
            log.info("【SQL查询】查询结果为空：where={}", whereMap);
            return null;
        }

        if (ts.size() > 1) {
            log.warn("【SQL查询】查询结果记录数：{},查询条件：{}", ts.size(), whereMap);
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "【SQL查询】查询结果记录数：" + ts.size());
        }
        return ts.get(0);
    }

    @Override
    public List<T> findList(T entity) {
        if (checkNull(entity)) {
            return new ArrayList<>();
        }
        Map<String, Object> whereMap = CommonUtils.getFieldVals(entity);
        log.info(SQL_WHERE_DESC, whereMap);
        this.setFieldExt(whereMap, entity);
        //分页的处理
        PageHelper.startPage(1, CommonUtils.getLimit(entity.getLimit()), false);
        PageHelper.orderBy(CommonUtils.getOrderBy(entity.getOrderBy()));
        return baseDao.selectList(whereMap);
    }

    @Override
    public PageInfo<T> findByPage(Page page, T entity) {
        if (checkNull(entity)) {
            return new PageInfo<>(new ArrayList<>());
        }
        Map<String, Object> whereMap = CommonUtils.getFieldVals(entity);
        log.info(SQL_WHERE_DESC, whereMap);
        this.setFieldExt(whereMap, entity);
        //分页的处理
        PageHelper.startPage(page.getPageNum(), CommonUtils.getLimit(page.getPageSize()), true);
        PageHelper.orderBy(CommonUtils.getOrderBy(entity.getOrderBy()));
        return new PageInfo<>(baseDao.selectByPage(whereMap));
    }

    @Override
    public Long count(T entity) {
        if (checkNull(entity)) {
            return -1L;
        }
        Map<String, Object> whereMap = CommonUtils.getFieldVals(entity);
        this.setFieldExt(whereMap, entity);
        return baseDao.count(whereMap);
    }

    @Transactional
    @Override
    public Long save(T entity) throws BusinessException {
        if (checkNull(entity)) {
            return -1L;
        }
        entity.setGmtCreate(new Date());
        entity.setGmtModified(entity.getGmtCreate());
        if (entity.getCreator() == null) {
            entity.setCreator("");
        }
        if (entity.getModifier() == null) {
            entity.setModifier("");
        }
        Long recordCount = baseDao.insert(entity);
        this.checkRecordCount(recordCount);
        return entity.getId();
    }

    @Transactional
    @Override
    public Long update(T entity) throws BusinessException {
        if (checkNull(entity)) {
            return -1L;
        }
        Map<String, Object> whereMap = CommonUtils.getFieldVals(entity);
        if (CollectionUtils.isEmpty(whereMap)) {
            log.warn("【SQL更新】查询条件为空：where={}", whereMap);
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "【SQL更新】查询条件为空：where=" + whereMap);
        }
        entity.setGmtModified(new Date());
        if (entity.getModifier() == null) {
            entity.setModifier("");
        }
        Long recordCount = baseDao.update(entity);
        this.checkRecordCount(recordCount);
        return recordCount;
    }

    @Transactional
    @Override
    public Long delete(Long id) throws BusinessException {
        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("id", id);
        whereMap.put("gmtModified", new Date());
        Long recordCount = baseDao.delete(whereMap);
        this.checkRecordCount(recordCount);
        return recordCount;
    }

    @Transactional
    @Override
    public Long delete(T entity) throws BusinessException {
        if (checkNull(entity)) {
            return -1L;
        }
        Map<String, Object> whereMap = CommonUtils.getFieldVals(entity);
        if (CollectionUtils.isEmpty(whereMap)) {
            log.warn("【SQL删除】查询条件为空：where={}", whereMap);
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "【SQL删除】查询条件为空：where=" + whereMap);
        }
        entity.setGmtModified(new Date());
        Long recordCount = baseDao.delete(CommonUtils.getFieldVals(entity));
        this.checkRecordCount(recordCount);
        return recordCount;
    }

    //检查参数是否为空
    private boolean checkNull(Object param) {
        if (param == null) {
            log.info("【SQL查询】查询条件为空：param={}", param);
            return true;
        }
        return false;
    }

    //校验Where条件是否为空
    private void checkWhereMap(Map<String, Object> whereMap) throws BusinessException {
        if (CollectionUtils.isEmpty(whereMap)) {
            log.warn("【SQL查询】查询条件为空：where={}", whereMap);
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "【SQL查询】查询条件为空：where=" + whereMap);
        }
    }

    //设置扩展字段
    private void setFieldExt(Map<String, Object> whereMap, T entity) {
        if (entity != null && !CollectionUtils.isEmpty(entity.getFieldExt())) {
            whereMap.putAll(entity.getFieldExt());
            entity.getFieldExt().clear();
        }
    }

    //校验影响的记录条数
    private void checkRecordCount(Long recordCount) throws BusinessException {
        if (recordCount == null || recordCount.longValue() < 1L) {
            log.warn("【SQL查询】未影响到任何记录：recordCount={}", recordCount);
            throw new BusinessException(HttpStatus.NOT_FOUND.value(), "未影响到任何记录：recordCount=" + recordCount);
        }
    }

    public void setBaseDao(BaseDao<T> baseDao) {
        this.baseDao = baseDao;
    }
}
