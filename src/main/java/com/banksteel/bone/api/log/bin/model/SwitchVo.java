package com.banksteel.bone.api.log.bin.model;

import java.util.Set;

/**
 * 开关的配置项
 *
 * @author 杨新伦
 * @date 2018-11-08
 */
public class SwitchVo {

    /**
     * 环境开关(ON：打开，OFF：关闭)
     */
    private String envState;
    /**
     * 应用开关：被排除的应用，多个应用英文名之间以逗号分隔
     */
    private String apps;
    /**
     * 应用开关：满足条件的应用是需要关闭的
     */
    private Set<String> appSet;

    /**
     * RPC服务开关(ON：打开，OFF：关闭)
     */
    private String rpc;
    /**
     * RPC服务开关(ON：打开，OFF：关闭)：参数
     */
    private String rpcParam;
    /**
     * RPC服务开关(ON：打开，OFF：关闭)：返回值
     */
    private String rpcResult;
    /**
     * RPC服务开关(ON：打开，OFF：关闭)：异常栈轨迹
     */
    private String rpcStackTrace;

    /**
     * HTTP客户端开关(ON：打开，OFF：关闭)
     */
    private String httpClient;
    /**
     * HTTP开关(ON：打开，OFF：关闭)：异常栈轨迹
     */
    private String httpStackTrace;

    /**
     * filter开关(ON：打开，OFF：关闭)
     */
    private String filter;
    /**
     * filter开关(ON：打开，OFF：关闭)：异常栈轨迹
     */
    private String filterStackTrace;

    /**
     * Web层开关(ON：打开，OFF：关闭)
     */
    private String web;
    /**
     * Web层开关(ON：打开，OFF：关闭)：参数
     */
    private String webParam;
    /**
     * Web层开关(ON：打开，OFF：关闭)：返回值
     */
    private String webResult;
    /**
     * Web层开关(ON：打开，OFF：关闭)：异常栈轨迹
     */
    private String webStackTrace;

    /**
     * 服务层开关(ON：打开，OFF：关闭)
     */
    private String service;
    /**
     * 服务层开关(ON：打开，OFF：关闭)：参数
     */
    private String serviceParam;
    /**
     * 服务层开关(ON：打开，OFF：关闭)：返回值
     */
    private String serviceResult;
    /**
     * 服务层开关(ON：打开，OFF：关闭)：异常栈轨迹
     */
    private String serviceStackTrace;

    /**
     * Dao层开关(ON：打开，OFF：关闭)
     */
    private String dao;
    /**
     * Dao层开关(ON：打开，OFF：关闭)：参数
     */
    private String daoParam;
    /**
     * Dao层开关(ON：打开，OFF：关闭)：返回值
     */
    private String daoResult;
    /**
     * Dao层开关(ON：打开，OFF：关闭)：异常栈轨迹
     */
    private String daoStackTrace;

    public String getEnvState() {
        return envState;
    }

    public void setEnvState(String envState) {
        this.envState = envState;
    }

    public String getApps() {
        return apps;
    }

    public void setApps(String apps) {
        this.apps = apps;
    }

    public Set<String> getAppSet() {
        return appSet;
    }

    public void setAppSet(Set<String> appSet) {
        this.appSet = appSet;
    }

    public String getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(String httpClient) {
        this.httpClient = httpClient;
    }

    public String getHttpStackTrace() {
        return httpStackTrace;
    }

    public void setHttpStackTrace(String httpStackTrace) {
        this.httpStackTrace = httpStackTrace;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilterStackTrace() {
        return filterStackTrace;
    }

    public void setFilterStackTrace(String filterStackTrace) {
        this.filterStackTrace = filterStackTrace;
    }

    public String getRpc() {
        return rpc;
    }

    public void setRpc(String rpc) {
        this.rpc = rpc;
    }

    public String getRpcParam() {
        return rpcParam;
    }

    public void setRpcParam(String rpcParam) {
        this.rpcParam = rpcParam;
    }

    public String getRpcResult() {
        return rpcResult;
    }

    public void setRpcResult(String rpcResult) {
        this.rpcResult = rpcResult;
    }

    public String getRpcStackTrace() {
        return rpcStackTrace;
    }

    public void setRpcStackTrace(String rpcStackTrace) {
        this.rpcStackTrace = rpcStackTrace;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getWebParam() {
        return webParam;
    }

    public void setWebParam(String webParam) {
        this.webParam = webParam;
    }

    public String getWebResult() {
        return webResult;
    }

    public void setWebResult(String webResult) {
        this.webResult = webResult;
    }

    public String getWebStackTrace() {
        return webStackTrace;
    }

    public void setWebStackTrace(String webStackTrace) {
        this.webStackTrace = webStackTrace;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getServiceParam() {
        return serviceParam;
    }

    public void setServiceParam(String serviceParam) {
        this.serviceParam = serviceParam;
    }

    public String getServiceResult() {
        return serviceResult;
    }

    public void setServiceResult(String serviceResult) {
        this.serviceResult = serviceResult;
    }

    public String getServiceStackTrace() {
        return serviceStackTrace;
    }

    public void setServiceStackTrace(String serviceStackTrace) {
        this.serviceStackTrace = serviceStackTrace;
    }

    public String getDao() {
        return dao;
    }

    public void setDao(String dao) {
        this.dao = dao;
    }

    public String getDaoParam() {
        return daoParam;
    }

    public void setDaoParam(String daoParam) {
        this.daoParam = daoParam;
    }

    public String getDaoResult() {
        return daoResult;
    }

    public void setDaoResult(String daoResult) {
        this.daoResult = daoResult;
    }

    public String getDaoStackTrace() {
        return daoStackTrace;
    }

    public void setDaoStackTrace(String daoStackTrace) {
        this.daoStackTrace = daoStackTrace;
    }

}
