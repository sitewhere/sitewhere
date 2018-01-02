/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.spring;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.configuration.parser.IInstanceManagementParser.MongoDbElements;
import com.sitewhere.mongodb.MongoConfiguration;
import com.sitewhere.spi.microservice.spring.InstanceGlobalBeans;

/**
 * Parses data for global MongoDB configurations that may be used by tenants.
 * 
 * @author Derek
 */
public class MongoConfigurationsParser extends AbstractBeanDefinitionParser {

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
	    MongoDbElements type = MongoDbElements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown MongoDB configuration element: " + child.getLocalName());
	    }
	    switch (type) {
	    case DefaultMongoConfiguration: {
		parseDefaultMongoConfiguration(child, context);
		break;
	    }
	    case AlternateMongoConfiguration: {
		parseAlternateMongoConfiguration(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse the default MongoDB configuration element.
     * 
     * @param element
     * @param context
     */
    protected void parseDefaultMongoConfiguration(Element element, ParserContext context) {
	BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(MongoConfiguration.class);
	parseMongoAttributes(element, context, configuration);
	context.getRegistry().registerBeanDefinition(InstanceGlobalBeans.BEAN_MONGO_CONFIGURATION_DEFAULT,
		configuration.getBeanDefinition());
    }

    /**
     * Parse an alternate MongoDB configuration element.
     * 
     * @param element
     * @param context
     */
    protected void parseAlternateMongoConfiguration(Element element, ParserContext context) {
	BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(MongoConfiguration.class);
	parseMongoAttributes(element, context, configuration);

	Attr id = element.getAttributeNode("id");
	if (id == null) {
	    throw new RuntimeException("No id specified for MongoDB alternate configuation.");
	}

	// Register bean using id as part of name.
	String beanName = InstanceGlobalBeans.BEAN_MONGO_CONFIGURATION_BASE + id.getValue();
	context.getRegistry().registerBeanDefinition(beanName, configuration.getBeanDefinition());
    }

    /**
     * Common parser logic for MongoDB attributes.
     * 
     * @param element
     * @param context
     * @param client
     */
    public static void parseMongoAttributes(Element element, ParserContext context, BeanDefinitionBuilder client) {
	Attr hostname = element.getAttributeNode("hostname");
	if (hostname != null) {
	    client.addPropertyValue("hostname", hostname.getValue());
	}
	Attr port = element.getAttributeNode("port");
	if (port != null) {
	    client.addPropertyValue("port", port.getValue());
	}
	Attr databaseName = element.getAttributeNode("databaseName");
	if (databaseName != null) {
	    client.addPropertyValue("databaseName", databaseName.getValue());
	}

	// Determine if username and password are supplied.
	Attr username = element.getAttributeNode("username");
	Attr password = element.getAttributeNode("password");
	if ((username != null) && ((password == null))) {
	    throw new RuntimeException("If username is specified for MongoDB, password must be specified as well.");
	}
	if ((username == null) && ((password != null))) {
	    throw new RuntimeException("If password is specified for MongoDB, username must be specified as well.");
	}
	if ((username != null) && (password != null)) {
	    client.addPropertyValue("username", username.getValue());
	    client.addPropertyValue("password", password.getValue());
	}

	Attr authDatabaseName = element.getAttributeNode("authDatabaseName");
	if (authDatabaseName != null) {
	    client.addPropertyValue("authDatabaseName", authDatabaseName.getValue());
	}

	// Set replica set name if specified.
	Attr replicaSetName = element.getAttributeNode("replicaSetName");
	if (replicaSetName != null) {
	    client.addPropertyValue("replicaSetName", replicaSetName.getValue());
	}

	// Determine if replication set should be created if does not exist.
	Attr autoConfigureReplication = element.getAttributeNode("autoConfigureReplication");
	if (autoConfigureReplication != null) {
	    client.addPropertyValue("autoConfigureReplication", autoConfigureReplication.getValue());
	}
    }
}