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
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * HTTP响应的切面
 *
 * @author 杨新伦
 * @date 2018-11-06
 */
@Slf4j
@Aspect
@Component("boneApiLogSpringControllerAspect")
@ConditionalOnWebApplication
public class SpringControllerAspect {

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
    @SuppressWarnings("squid:S1181")
    @Around("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        InterfaceVo entity = null;
        HttpServletResponse response = null;
        try {
            HttpServletRequest request = null;
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                request = requestAttributes.getRequest();
                response = requestAttributes.getResponse();
            }
            if (apiLogProperties != null && switchHelper != null && switchHelper.checkWeb()) {
                entity = this.getInterface(joinPoint, request);
            }
        } catch (Throwable e) {
            appLog.warn("SpringControllerAspect异常", e);
        }
        try {
            Object result = joinPoint.proceed();
            this.postInterface(entity, joinPoint, result, response);
            return result;
        } catch (Throwable throwable) {
            this.postInterfaceException(entity, throwable);
            throw throwable;
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterface(InterfaceVo entity, ProceedingJoinPoint joinPoint, Object result, HttpServletResponse response) {
        try {
            if (entity != null) {
                interfaceHelper.setInterfaceResult(switchHelper.checkWebResult(), joinPoint, entity, result);
                if (response != null) {
                    entity.setState(response.getStatus());
                }
                log.info(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e) {
            appLog.warn("SpringControllerAspect.postInterface处理异常", e);
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterfaceException(InterfaceVo entity, Throwable throwable) {
        try {
            if (entity != null) {
                interfaceHelper.setInterfaceException(switchHelper.checkWebResult(), entity, throwable);
                entity.setStackTrace(switchHelper.checkWebStackTrace() ? ExceptionUtils.getFullStackTrace(throwable) : null);
                log.error(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e1) {
            appLog.warn("SpringControllerAspect.postInterfaceException处理异常", e1);
        }
    }

    private InterfaceVo getInterface(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
        InterfaceVo entity = new InterfaceVo(apiLogProperties);
        entity.setEndpointType(StringUtils.hasText(request.getHeader("X-Bone-traceId")) ? EndpointTypeEnum.SERVER.name() : EndpointTypeEnum.CONTROLLER.name());
        entity.setInterfaceType(InterfaceTypeEnum.HTTP.name());
        interfaceHelper.setInterfaceName(entity, joinPoint.getSignature().toString());
        interfaceHelper.setInterfaceParams(switchHelper.checkWebParam(), entity, joinPoint.getArgs());
        this.setUserInfos(request, entity);
        return entity;
    }

    private void setUserInfos(HttpServletRequest request, InterfaceVo entity) {
        if (request == null) {
            return;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return;
        }
        for (Cookie tmp : cookies) {
            if (Objects.equals(apiLogProperties.getUserId(), tmp.getName())) {
                entity.setUserId(tmp.getValue());
            } else if (Objects.equals(apiLogProperties.getAccount(), tmp.getName())) {
                entity.setAccount(tmp.getValue());
            }
        }
    }

}
