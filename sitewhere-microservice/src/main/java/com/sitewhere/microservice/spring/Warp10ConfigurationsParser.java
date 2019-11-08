/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.spring;

import com.sitewhere.configuration.datastore.DatastoreConfigurationParser;
import com.sitewhere.configuration.instance.warp10db.Warp10Configuration;
import com.sitewhere.configuration.parser.IInstanceManagementParser.Warp10DbElements;
import com.sitewhere.spi.microservice.spring.InstanceManagementBeans;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Parses data for global Warp 10 configurations that may be used by tenants.
 *
 * @author Derek
 */
public class Warp10ConfigurationsParser extends AbstractBeanDefinitionParser {

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal(org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
        List<Element> children = DomUtils.getChildElements(element);
        for (Element child : children) {
            Warp10DbElements type = Warp10DbElements.getByLocalName(child.getLocalName());
            if (type == null) {
                throw new RuntimeException("Unknown warp 10 configuration element: " + child.getLocalName());
            }
            switch (type) {
                case Warp10Configuration: {
                    parseWarp10Configuration(child, context);
                    break;
                }
            }
        }
        return null;
    }

    /**
     * Parse an InfluxDB configuration element.
     *
     * @param element
     * @param context
     */
    protected void parseWarp10Configuration(Element element, ParserContext context) {
        BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(Warp10Configuration.class);
        DatastoreConfigurationParser.parseWarp10Attributes(element, context, configuration);

        Attr id = element.getAttributeNode("id");
        if (id == null) {
            throw new RuntimeException("No id specified for warp10DB configuration.");
        }

        // Register bean using id as part of name.
        String beanName = InstanceManagementBeans.BEAN_WARP10_CONFIGURATION_BASE + id.getValue();
        context.getRegistry().registerBeanDefinition(beanName, configuration.getBeanDefinition());
    }

}