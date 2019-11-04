/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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
	if(context == null) {
	    System.err.println("The DbManager has not started yet!");
	}
	return context.getBean(t);
    }
}
