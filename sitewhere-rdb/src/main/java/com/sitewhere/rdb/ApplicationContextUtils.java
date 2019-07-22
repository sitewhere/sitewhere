package com.sitewhere.rdb;

import org.springframework.context.ApplicationContext;

/**
 * Application context Util
 *
 * Simeon Chen
 */
public class ApplicationContextUtils {
    private static ApplicationContext context;

    public static void setContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> t) {
        return context.getBean(beanName, t);
    }

    public static <T> T getBean(Class<T> t) {
        return context.getBean(t);
    }
}
