package com.banksteel.bone.api.log.bin.constant;

/**
 * 请求状态枚举类
 *
 * @author 杨新伦
 * @date 2018-11-05
 */
public enum RequestStateEnum {
    OK(200), ERROR(500);
    private Integer statusCode;

    RequestStateEnum(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
