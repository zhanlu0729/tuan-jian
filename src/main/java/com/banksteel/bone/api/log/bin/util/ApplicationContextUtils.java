package com.banksteel.bone.api.log.bin.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring容器工具类
 *
 * @author 杨新伦
 * @date 2018-04-16
 */
@Component("boneApiLogApplicationContextUtils")
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static boolean containsBean(String name) {
        return ApplicationContextUtils.applicationContext != null && ApplicationContextUtils.applicationContext.containsBean(name);
    }

    public static boolean containsBeanDefinition(String name) {
        return ApplicationContextUtils.applicationContext != null && ApplicationContextUtils.applicationContext.containsBeanDefinition(name);
    }

    public static boolean containsLocalBean(String name) {
        return ApplicationContextUtils.applicationContext != null && ApplicationContextUtils.applicationContext.containsLocalBean(name);
    }

    public static Object getBean(String name) {
        return ApplicationContextUtils.applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return ApplicationContextUtils.applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return ApplicationContextUtils.applicationContext.getBean(name, clazz);
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) {
        if (ApplicationContextUtils.applicationContext == null) {
            ApplicationContextUtils.applicationContext = applicationContext;
        }
    }

}
