package com.tuanjian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderByEnum {

    ASC(0, "正序"), DESC(1, "倒序");

    private int value;
    private String desc;

    public static OrderByEnum toEnum(String[] tmpArr) {
        if (tmpArr != null && tmpArr.length > 1 && tmpArr[1].equals(String.valueOf(OrderByEnum.ASC.value))) {
            return OrderByEnum.ASC;
        }
        return OrderByEnum.DESC;
    }
}
