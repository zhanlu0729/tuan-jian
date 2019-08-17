package com.banksteel.bone.api.log.bin.aspect;

import com.banksteel.bone.api.log.bin.constant.EndpointTypeEnum;
import com.banksteel.bone.api.log.bin.constant.InterfaceTypeEnum;
import com.banksteel.bone.api.log.bin.model.InterfaceVo;
import com.banksteel.bone.api.log.bin.properties.ApiLogProperties;
import com.banksteel.bone.api.log.bin.util.InterfaceHelper;
import com.banksteel.bone.api.log.bin.util.SwitchHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * HTTP响应的切面
 *
 * @author 杨新伦
 * @date 2018-11-06
 */
@Slf4j
@Aspect
@Component("boneApiLogSpringServiceAspect")
public class SpringServiceAspect {

    private static final Logger appLog = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    @Autowired
    private ApiLogProperties apiLogProperties;
    @Autowired
    private SwitchHelper switchHelper;
    @Autowired
    private InterfaceHelper interfaceHelper;

    /**
     * 环绕通知
     */
    @Around("execution(public * com.banksteel.bone.common.*Service.*(..)) || execution(public * com.banksteel..service..*(..)) || " +
            "@within(org.springframework.stereotype.Service)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        InterfaceVo entity = this.getInterfaceVo(joinPoint);
        try {
            Object result = joinPoint.proceed();
            this.postInterface(entity, joinPoint, result);
            return result;
        } catch (Throwable throwable) {
            this.postInterfaceException(entity, throwable);
            throw throwable;
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterface(InterfaceVo entity, ProceedingJoinPoint joinPoint, Object result) {
        try {
            if (entity != null) {
                interfaceHelper.setInterfaceResult(switchHelper.checkServiceResult(), joinPoint, entity, result);
                log.info(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e) {
            appLog.warn("SpringServiceAspect.postInterface处理异常", e);
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterfaceException(InterfaceVo entity, Throwable throwable) {
        try {
            if (entity != null) {
                interfaceHelper.setInterfaceException(switchHelper.checkServiceResult(), entity, throwable);
                entity.setStackTrace(switchHelper.checkServiceStackTrace() ? ExceptionUtils.getFullStackTrace(throwable) : null);
                log.error(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e1) {
            appLog.warn("SpringServiceAspect.postInterfaceException处理异常", e1);
        }
    }

    @SuppressWarnings("squid:S1181")
    private InterfaceVo getInterfaceVo(ProceedingJoinPoint joinPoint) {
        InterfaceVo entity = null;
        try {
            if (apiLogProperties != null && switchHelper != null && switchHelper.checkService()) {
                entity = new InterfaceVo(apiLogProperties);
                entity.setEndpointType(EndpointTypeEnum.SERVICE.name());
                entity.setInterfaceType(InterfaceTypeEnum.INNER.name());
                interfaceHelper.setInterfaceName(entity, joinPoint.getSignature().toString());
                interfaceHelper.setInterfaceParams(switchHelper.checkServiceParam(), entity, joinPoint.getArgs());
            }
        } catch (Throwable e) {
            appLog.warn("SpringServiceAspect异常", e);
        }
        return entity;
    }
}
