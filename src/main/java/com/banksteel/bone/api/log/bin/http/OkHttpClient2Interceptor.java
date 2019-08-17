package com.banksteel.bone.api.log.bin.http;

import com.banksteel.bone.api.log.bin.constant.EndpointTypeEnum;
import com.banksteel.bone.api.log.bin.constant.InterfaceTypeEnum;
import com.banksteel.bone.api.log.bin.model.InterfaceVo;
import com.banksteel.bone.api.log.bin.properties.ApiLogProperties;
import com.banksteel.bone.api.log.bin.util.InterfaceHelper;
import com.banksteel.bone.api.log.bin.util.SwitchHelper;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OkHttp 2.x拦截器
 *
 * @author 杨新伦
 * @date 2018-11-06
 */
@Slf4j
@Component("boneApiLogOkHttpClient2Interceptor")
@ConditionalOnClass(value = OkHttpClient.class)
public class OkHttpClient2Interceptor implements Interceptor {

    private static final Logger appLog = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    @Autowired
    private ApiLogProperties apiLogProperties;
    @Autowired
    private SwitchHelper switchHelper;
    @Autowired
    private InterfaceHelper interfaceHelper;

    @SuppressWarnings("squid:S1181")
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        InterfaceVo entity = null;
        try {
            if (apiLogProperties != null && switchHelper != null && switchHelper.checkHttpClient()) {
                entity = new InterfaceVo(apiLogProperties);
                entity.setEndpointType(EndpointTypeEnum.CLIENT.name());
                entity.setInterfaceType(InterfaceTypeEnum.HTTP.name());
                entity.setInterfaceName(request.uri().getPath());
                entity.setParamValues(request.uri().getQuery());
            }
        } catch (Throwable e) {
            appLog.warn("OkHttp2客户端请求异常", e);
        }

        try {
            Response response = chain.proceed(request);
            if (entity != null) {
                this.postInterface(entity, response);
            }
            return response;
        } catch (IOException e2) {
            if (entity != null) {
                this.postInterfaceException(entity, e2);
            }
            throw e2;
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterface(InterfaceVo entity, Response response) {
        try {
            if (entity != null) {
                if (response != null) {
                    entity.setState(response.code());
                }
                log.info(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e) {
            appLog.warn("OkHttpClient2Interceptor.postInterface处理异常", e);
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterfaceException(InterfaceVo entity, IOException e2) {
        try {
            if (entity != null) {
                interfaceHelper.setInterfaceException(switchHelper.checkHttpClient(), entity, e2);
                entity.setStackTrace(switchHelper.checkHttpStackTrace() ? ExceptionUtils.getFullStackTrace(e2) : null);
                log.error(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e1) {
            appLog.warn("OkHttpClient2Interceptor.postInterfaceException处理异常", e1);
        }
    }
}
