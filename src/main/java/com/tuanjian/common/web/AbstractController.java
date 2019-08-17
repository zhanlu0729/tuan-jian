package com.tuanjian.common.web;

import com.tuanjian.common.model.BaseModel;
import com.tuanjian.common.service.BaseService;
import com.tuanjian.common.utils.CommonUtils;
import com.tuanjian.common.utils.JsonResult;
import com.tuanjian.common.utils.Page;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
public abstract class AbstractController<M extends BaseModel> {

    @Autowired
    protected ApplicationContext applicationContext;
    protected BaseService<M> baseService;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void initBaseService() {
        // 获取当前new的对象的泛型的父类类型
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取第一个类型参数的真实类型
        Class<M> dtoClass = (Class<M>) pt.getActualTypeArguments()[0];
        String bizName = CommonUtils.getDefaultBeanName(dtoClass.getSimpleName()).replace("DTO", "") + "Biz";
        baseService =
            applicationContext.containsBean(bizName) ? (BaseService<M>) applicationContext.getBean(bizName) : null;
        if (baseService == null) {
            log.error("【baseService】为空：{}", dtoClass);
        }
    }

    /**
     * 根据ID查询单个对象
     */
    @GetMapping("/{id}")
    public JsonResult getOne(@PathVariable("id") Long id) {
        M entity = baseService.getOne(id);
        return JsonResult.builder().withData(entity).build();
    }

    /**
     * 查询多个对象
     */
    @GetMapping(value = "/list")
    public JsonResult findList(M dto) {
        List<M> list = baseService.findList(dto);
        return JsonResult.builder().withData(list).build();
    }

    /**
     * 分页查询多个对象
     */
    @GetMapping
    public JsonResult findByPage(Page page, M dto) {
        return JsonResult.builder().withData(baseService.findByPage(page, dto)).build();
    }

    /**
     * 保存单个新对象
     */
    @PostMapping
    public JsonResult save(@RequestBody M dto) {
        return JsonResult.builder().withData(baseService.save(dto)).build();
    }

    /**
     * 更新单个对象
     */
    @PutMapping("/{id}")
    public JsonResult update(@PathVariable("id") Long id, @RequestBody M dto) {
        dto.setId(id);
        return JsonResult.builder().withData(baseService.update(dto)).build();
    }

    /**
     * 删除单个对象
     */
    @DeleteMapping("/{id}")
    public JsonResult delete(@PathVariable("id") Long id) {
        return JsonResult.builder().withData(baseService.delete(id)).build();
    }

}
