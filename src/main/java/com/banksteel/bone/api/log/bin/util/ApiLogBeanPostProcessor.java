package com.banksteel.bone.api.log.bin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Spring的扩展接口：消除业务方对全链路跟踪API的显示依赖
 *
 * @author 杨新伦
 * @date 2018-11-06
 */
@Component("boneApiLogBeanPostProcessor")
public class ApiLogBeanPostProcessor implements org.springframework.beans.factory.config.BeanPostProcessor {

    private static final Logger appLog = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @SuppressWarnings("squid:S1181")
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        try {
            Class<?> beanClazz = bean.getClass();
            if (AopUtils.isAopProxy(bean)) {
                beanClazz = AopUtils.getTargetClass(bean);
            }
            String clazzName = beanClazz.getCanonicalName();
            if (StringUtils.isEmpty(clazzName)) {
                return bean;
            }

            //OkHttp 2.x
            if (clazzName.equals("com.squareup.okhttp.OkHttpClient")) {
                com.squareup.okhttp.OkHttpClient httpClient = (com.squareup.okhttp.OkHttpClient) bean;
                httpClient.interceptors().add(applicationContext.getBean("boneApiLogOkHttpClient2Interceptor", com.squareup.okhttp.Interceptor.class));
            }

            //OkHttp 3.x Builder
            else if (clazzName.equals("okhttp3.OkHttpClient.Builder")) {
                okhttp3.OkHttpClient.Builder builder = (okhttp3.OkHttpClient.Builder) bean;
                builder.addInterceptor(applicationContext.getBean("boneApiLogOkHttpClient3Interceptor", okhttp3.Interceptor.class));
            }

            //Spring RestTemplate
            else if (clazzName.equals("org.springframework.web.client.RestTemplate")) {
                org.springframework.web.client.RestTemplate restTemplate = (org.springframework.web.client.RestTemplate) bean;
                restTemplate.getInterceptors().add(applicationContext.getBean("boneApiLogSpringRestTemplateRequestInterceptor", org.springframework.http.client.ClientHttpRequestInterceptor.class));
            }

            //Apache HttpClientBuilder
            else if (clazzName.equals("org.apache.http.impl.client.HttpClientBuilder")) {
                org.apache.http.impl.client.HttpClientBuilder builder = (org.apache.http.impl.client.HttpClientBuilder) bean;
                builder.addInterceptorLast(applicationContext.getBean("boneApiLogApacheHttpClientRequestInterceptor", org.apache.http.HttpRequestInterceptor.class));
            }
        } catch (Throwable e) {
            appLog.info("【" + beanName + "】属性扩展失败", e);
        }
        return bean;
    }

}
