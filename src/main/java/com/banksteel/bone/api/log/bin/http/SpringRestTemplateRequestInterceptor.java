package com.banksteel.bone.api.log.bin.http;

import com.banksteel.bone.api.log.bin.constant.EndpointTypeEnum;
import com.banksteel.bone.api.log.bin.constant.InterfaceTypeEnum;
import com.banksteel.bone.api.log.bin.model.InterfaceVo;
import com.banksteel.bone.api.log.bin.properties.ApiLogProperties;
import com.banksteel.bone.api.log.bin.util.InterfaceHelper;
import com.banksteel.bone.api.log.bin.util.SwitchHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;

/**
 * RestTemplate拦截器
 *
 * @author 杨新伦
 * @date 2018-11-06
 */
@Slf4j
@Component("boneApiLogSpringRestTemplateRequestInterceptor")
@ConditionalOnClass(value = RestTemplate.class)
public class SpringRestTemplateRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger appLog = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    @Autowired
    private ApiLogProperties apiLogProperties;
    @Autowired
    private SwitchHelper switchHelper;
    @Autowired
    private InterfaceHelper interfaceHelper;

    @SuppressWarnings("squid:S1181")
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        InterfaceVo entity = null;
        try {
            if (apiLogProperties != null && switchHelper != null && switchHelper.checkHttpClient()) {
                entity = new InterfaceVo(apiLogProperties);
                entity.setStartTime(new Date());
                entity.setEndpointType(EndpointTypeEnum.CLIENT.name());
                entity.setInterfaceType(InterfaceTypeEnum.HTTP.name());
                entity.setInterfaceName(request.getURI().getPath());
                entity.setParamValues(request.getURI().getQuery());
            }
        } catch (Throwable e) {
            appLog.warn("SpringRestTemplate客户端请求异常", e);
        }

        try {
            ClientHttpResponse response = execution.execute(request, body);
            this.postInterface(entity, response);
            return response;
        } catch (IOException e) {
            this.postInterfaceException(entity, e);
            throw e;
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterface(InterfaceVo entity, ClientHttpResponse response) {
        try {
            if (entity != null) {
                entity.setState(response.getStatusCode().value());
                log.info(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e) {
            appLog.warn("SpringRestTemplateRequestInterceptor.postInterface处理异常", e);
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterfaceException(InterfaceVo entity, IOException e) {
        try {
            if (entity != null) {
                interfaceHelper.setInterfaceException(switchHelper.checkHttpClient(), entity, e);
                entity.setStackTrace(switchHelper.checkHttpStackTrace() ? ExceptionUtils.getFullStackTrace(e) : null);
                log.error(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e1) {
            appLog.warn("SpringRestTemplateRequestInterceptor.postInterfaceException处理异常", e1);
        }
    }
}
