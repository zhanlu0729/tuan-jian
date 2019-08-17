package com.banksteel.bone.api.log.bin.model;

import com.banksteel.bone.api.log.bin.properties.ApiLogProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 接口的值对象
 *
 * @author 杨新伦
 * @date 2018-11-05
 */
@Getter
@Setter
public class InterfaceVo {

    public InterfaceVo() {
        this.startTime = new Date();
    }

    public InterfaceVo(ApiLogProperties apiLogProperties) {
        this();
        this.clusterCode = apiLogProperties.getClusterCode();
        this.env = apiLogProperties.getEnv();
        this.hostName = apiLogProperties.getHostName();
        this.appName = apiLogProperties.getAppName();
        this.deptCode = apiLogProperties.getDeptCode();
    }

    /**
     * 调用状态：参考HTTP状态码
     */
    private Integer state;
    /**
     * 集群编号：gy、gl、lz
     */
    private String clusterCode;
    /**
     * 环境
     */
    private String env;
    /**
     * 主机号
     */
    private String hostName;
    /**
     * 应用名
     */
    private String appName;
    /**
     * 部门编号
     */
    private String deptCode;
    /**
     * 端点类型：参见EndpointTypeEnum
     */
    private String endpointType;
    /**
     * 接口类型：参见InterfaceTypeEnum
     */
    private String interfaceType;
    /**
     * 接口名：Web类应用中=URL路径,Dubbo类应用中=接口全限定名(参数列表)
     */
    private String interfaceName;
    /**
     * 接口的参数值
     */
    private String paramValues;
    /**
     * 接口的返回值
     */
    private String returnValue;
    /**
     * 开始时间：yyyy-MM-dd HH:mm:ss.SSS
     */
    private Date startTime;
    /**
     * 结束时间：yyyy-MM-dd HH:mm:ss.SSS
     */
    private Date endTime;
    /**
     * 总消耗时间：单位(毫秒)
     */
    private Long cost;
    /**
     * 附加的描述信息
     */
    private String remark;
    /**
     * 异常栈轨迹
     */
    private String stackTrace;

    /**
     * 调用接口的账号
     */
    private String account;
    /**
     * 调用接口的用户ID
     */
    private String userId;
}
