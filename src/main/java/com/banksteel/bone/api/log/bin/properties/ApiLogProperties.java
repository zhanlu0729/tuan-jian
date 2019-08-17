package com.banksteel.bone.api.log.bin.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 应用之间网络请求成本的配置项
 *
 * @author 杨新伦
 * @date 2018-11-05
 */
@Component("boneApiLogProperties")
@ConfigurationProperties(prefix = "bone.cookie")
public class ApiLogProperties {

    /**
     * 集群编号：gy、gl、lz
     */
    @Value("${bone.appSrc:gy}")
    private String clusterCode;
    /**
     * 应用所在环境
     */
    @Value("${env:dev}")
    private String env;
    /**
     * 应用英文名
     */
    @Value("${app.name:app}")
    private String appName;
    /**
     * 应用所在机器主机名
     */
    @Value("${HOSTNAME:unknown}")
    private String hostName;
    /**
     * 部门编号
     */
    @Value("${bone.deptCode:-}")
    private String deptCode;

    /**
     * Cookie中的域
     */
    private String domain;
    /**
     * 用户ID的Cookie名称
     */
    private String userId;
    /**
     * 用户账号的Cookie名称
     */
    private String account;

    public String getDomain() {
        if (StringUtils.isEmpty(domain)) {
            domain = ".banksteel.com";
        }
        return domain;
    }

    public String getUserId() {
        if (StringUtils.isEmpty(userId)) {
            userId = "_eid_";
        }
        return userId;
    }

    public String getAccount() {
        if (StringUtils.isEmpty(account)) {
            account = "_uname_";
        }
        return account;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public void setClusterCode(String clusterCode) {
        this.clusterCode = clusterCode;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }
}
