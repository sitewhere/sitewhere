/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parser that produces a list of beans in a {@link ManagedList}.
 * 
 * @author Derek
 */
public abstract class SiteWhereBeanListParser {

    /** Map classes that override root bean classes */
    private Map<Class<?>, Class<?>> beanMappings = new HashMap<Class<?>, Class<?>>();

    /**
     * Parse element contents into a {@link ManagedList}.
     * 
     * @param element
     * @param context
     * @return
     */
    public abstract ManagedList<?> parse(Element element, ParserContext context);

    /**
     * Returns a builder for the given bean, allowing for class override if
     * needed.
     * 
     * @param clazz
     * @return
     */
    protected BeanDefinitionBuilder getBuilderFor(Class<?> clazz) {
	Class<?> override = beanMappings.get(clazz);
	Class<?> used = (override == null) ? clazz : override;
	return BeanDefinitionBuilder.rootBeanDefinition(used);
    }

    public Map<Class<?>, Class<?>> getBeanMappings() {
	return beanMappings;
    }

    public void setBeanMappings(Map<Class<?>, Class<?>> beanMappings) {
	this.beanMappings = beanMappings;
    }
}
