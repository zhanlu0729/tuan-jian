package com.banksteel.bone.api.log.bin.http;

import com.banksteel.bone.api.log.bin.constant.EndpointTypeEnum;
import com.banksteel.bone.api.log.bin.constant.InterfaceTypeEnum;
import com.banksteel.bone.api.log.bin.constant.RequestStateEnum;
import com.banksteel.bone.api.log.bin.model.InterfaceVo;
import com.banksteel.bone.api.log.bin.properties.ApiLogProperties;
import com.banksteel.bone.api.log.bin.util.InterfaceHelper;
import com.banksteel.bone.api.log.bin.util.SwitchHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.HttpClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Apache HttpClient执行切面
 *
 * @author 杨新伦
 * @date 2018-11-06
 */
@Slf4j
@Aspect
@Component("boneApiLogApacheHttpClientResponseAspect")
@ConditionalOnClass(value = HttpClient.class)
public class ApacheHttpClientResponseAspect {

    private static final Logger appLog = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    @Autowired
    private ApiLogProperties apiLogProperties;
    @Autowired
    private SwitchHelper switchHelper;
    @Autowired
    private InterfaceHelper interfaceHelper;

    /**
     * 环绕通知
     *
     * @param joinPoint 连接点
     */
    @Around("execution(public * org.apache.http.client.HttpClient.execute(..))")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        InterfaceVo entity = this.getInterfaceVo(joinPoint);
        try {
            Object result = joinPoint.proceed();
            this.postInterface(entity);
            return result;
        } catch (Throwable throwable) {
            this.postInterfaceException(entity, throwable);
            throw throwable;
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterface(InterfaceVo entity) {
        try {
            if (entity != null && switchHelper.checkHttpClient()) {
                entity.setState(RequestStateEnum.OK.getStatusCode());
                log.info(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e) {
            appLog.warn("ApacheHttpClientResponseAspect.postInterface处理异常", e);
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterfaceException(InterfaceVo entity, Throwable throwable) {
        try {
            if (entity != null && switchHelper.checkHttpClient()) {
                interfaceHelper.setInterfaceException(switchHelper.checkHttpClient(), entity, throwable);
                entity.setStackTrace(switchHelper.checkHttpStackTrace() ? ExceptionUtils.getFullStackTrace(throwable) : null);
                log.error(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e1) {
            appLog.warn("ApacheHttpClientResponseAspect.postInterfaceException处理异常", e1);
        }
    }

    @SuppressWarnings("squid:S1181")
    private InterfaceVo getInterfaceVo(ProceedingJoinPoint joinPoint) {
        InterfaceVo entity = null;
        try {
            if (apiLogProperties != null && switchHelper != null && switchHelper.checkHttpClient()) {
                entity = new InterfaceVo(apiLogProperties);
                entity.setEndpointType(EndpointTypeEnum.CLIENT.name());
                entity.setInterfaceType(InterfaceTypeEnum.HTTP.name());
                interfaceHelper.setInterfaceName(entity, joinPoint.getSignature().toString());
                interfaceHelper.setInterfaceParams(switchHelper.checkWebParam(), entity, joinPoint.getArgs());
            }
        } catch (Throwable e) {
            appLog.warn("ApacheHttpClient客户端请求异常", e);
        }
        return entity;
    }

}
