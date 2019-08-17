package com.tuanjian.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class FieldUtils {

    private FieldUtils() {

    }

    private static final Map<Class<?>, List<Field>> CLASS_FIELD_MAP = new ConcurrentHashMap<>();

    /**
     * 获取class中的所有非静态属性列表
     *
     * @param clazz
     * @return
     */
    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fieldList = CLASS_FIELD_MAP.get(clazz);
        if (fieldList == null) {
            fieldList = new ArrayList<>();
            setFields(clazz, fieldList);
            CLASS_FIELD_MAP.put(clazz, fieldList);
        }
        return fieldList;
    }

    /**
     * 获取对象特定的field的值
     *
     * @param entity
     * @param field
     * @return
     */
    public static Object getFieldVal(Object entity, Field field) {
        Object retVal = null;
        try {
            field.setAccessible(true);
            retVal = field.get(entity);
        } catch (Exception e) {
            log.warn("获取属性值失败：" + entity.getClass().getSimpleName() + "." + field.getName(), e);
        }
        return retVal;
    }

    //递归获取当前类及父类所有非静态Field列表
    private static void setFields(Class<?> clazz, List<Field> fieldList) {
        if (clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.toString(field.getModifiers()).contains(" static ")) {//private static final
                continue;
            }
            fieldList.add(field);
        }
        setFields(clazz.getSuperclass(), fieldList);
    }

}
