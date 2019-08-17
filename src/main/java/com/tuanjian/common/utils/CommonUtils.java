package com.tuanjian.common.utils;

import com.tuanjian.common.enums.OrderByEnum;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

public final class CommonUtils {

    public static final int DEFAULT_LIMIT = 20;
    public static final int DEFAULT_MAX_LIMIT = 100;

    private CommonUtils() {

    }

    /**
     * 获取实体类的所有属性及值
     *
     * @param entity 实体
     */
    public static Map<String, Object> getFieldVals(Object entity) {
        Map<String, Object> fieldVals = new HashMap<>(16);
        List<Field> fieldList = FieldUtils.getFields(entity.getClass());
        for (Field field : fieldList) {
            if (field.getName().equals("fieldExt")) {
                continue;
            }
            Object result = FieldUtils.getFieldVal(entity, field);
            if (result != null) {
                fieldVals.put(field.getName(), result);
            }
        }
        return fieldVals;
    }

    /**
     * 获取Spring容器默认产生的Bean名字
     *
     * @param clazzName 类名
     * @return 默认Bean名字
     */
    public static String getDefaultBeanName(String clazzName) {
        if (clazzName.contains(".")) {
            String[] nameArr = clazzName.split("\\.");
            return nameArr[nameArr.length - 1].substring(0, 1).toLowerCase() + nameArr[nameArr.length - 1].substring(1);
        }
        return clazzName.substring(0, 1).toLowerCase() + clazzName.substring(1);
    }

    /**
     * 获取ordersBy属性
     *
     * @param orderBy 排序属性(1:DESC, 0:ASC)：列名:0|1
     * @return
     */
    public static String getOrderBy(String orderBy) {
        if (!StringUtils.hasText(orderBy)) {
            return "a.id DESC";
        }
        StringBuilder fieldBuf = new StringBuilder();
        String[] fieldArr = orderBy.split(",");
        for (int i = 0; i < fieldArr.length; i++) {
            String field = fieldArr[i];
            String[] tmpArr = field.split(":");
            if (i > 0) {
                fieldBuf.append(",");
            }
            fieldBuf.append("a." + tmpArr[0]);
            fieldBuf.append(" " + OrderByEnum.toEnum(tmpArr).name());
        }
        return fieldBuf.toString();
    }

    /**
     * 获取记录上限
     *
     * @param limit
     * @return
     */
    public static int getLimit(Integer limit) {
        if (limit == null || limit.intValue() < 1) {
            limit = DEFAULT_LIMIT;
        } else if (limit.intValue() > DEFAULT_MAX_LIMIT) {
            limit = DEFAULT_MAX_LIMIT;
        }
        return limit;
    }

}
