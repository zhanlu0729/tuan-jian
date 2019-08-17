package com.tuanjian.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zhoujia
 * <p>
 * 请求处理返回内容
 */
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JsonResult {

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息说明
     */
    private String msg;

    /**
     * 业务数据
     */
    private Object data;

    /**
     * 创建构建器
     *
     * @return Builder
     */
    public static Builder builder() {
        return Builder.newBuilder();
    }

    /**
     * JsonResult对应的链式语法构造器
     */
    public static class Builder {

        JsonResult instance = new JsonResult();

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withCode(int code) {
            this.instance.setCode(code);
            return this;
        }

        public Builder withMsg(String msg) {
            this.instance.setMsg(msg);
            return this;
        }

        public Builder withData(Object data) {
            this.instance.setData(data);
            return this;
        }

        public JsonResult build() {
            return instance;
        }
    }

}
