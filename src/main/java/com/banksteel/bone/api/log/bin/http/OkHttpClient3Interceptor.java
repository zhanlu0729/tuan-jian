package com.banksteel.bone.api.log.bin.http;

import com.banksteel.bone.api.log.bin.constant.EndpointTypeEnum;
import com.banksteel.bone.api.log.bin.constant.InterfaceTypeEnum;
import com.banksteel.bone.api.log.bin.model.InterfaceVo;
import com.banksteel.bone.api.log.bin.properties.ApiLogProperties;
import com.banksteel.bone.api.log.bin.util.InterfaceHelper;
import com.banksteel.bone.api.log.bin.util.SwitchHelper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OkHttp 3.x拦截器
 *
 * @author 杨新伦
 * @date 2018-11-06
 */
@Slf4j
@Component("boneApiLogOkHttpClient3Interceptor")
@ConditionalOnClass(value = OkHttpClient.class)
public class OkHttpClient3Interceptor implements Interceptor {

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
                entity.setInterfaceName(request.url().uri().getPath());
                entity.setParamValues(request.url().uri().getQuery());
            }
        } catch (Throwable e) {
            appLog.warn("OkHttp3客户端请求异常", e);
        }

        try {
            Response response = chain.proceed(request);
            this.postInterface(entity, response);
            return response;
        } catch (IOException e3) {
            this.postInterfaceException(entity, e3);
            throw e3;
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterface(InterfaceVo entity, Response response) {
        try {
            if (entity != null && response != null) {
                entity.setState(response.code());
                log.info(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e) {
            appLog.warn("OkHttpClient3Interceptor.postInterface处理异常", e);
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterfaceException(InterfaceVo entity, IOException e3) {
        try {
            if (entity != null) {
                interfaceHelper.setInterfaceException(switchHelper.checkHttpClient(), entity, e3);
                entity.setStackTrace(switchHelper.checkHttpStackTrace() ? ExceptionUtils.getFullStackTrace(e3) : null);
                log.error(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e1) {
            appLog.warn("OkHttpClient3Interceptor.postInterfaceException处理异常", e1);
        }
    }
}
