/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spring.handler;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.groovy.tenant.GroovyTenantModelInitializer;
import com.sitewhere.groovy.user.GroovyUserModelInitializer;
import com.sitewhere.hbase.DefaultHBaseClient;
import com.sitewhere.hbase.tenant.HBaseTenantManagement;
import com.sitewhere.hbase.user.HBaseUserManagement;
import com.sitewhere.mongodb.DockerMongoClient;
import com.sitewhere.mongodb.SiteWhereMongoClient;
import com.sitewhere.mongodb.tenant.MongoTenantManagement;
import com.sitewhere.mongodb.user.MongoUserManagement;
import com.sitewhere.server.SiteWhereServerBeans;
import com.sitewhere.server.tenant.DefaultTenantModelInitializer;
import com.sitewhere.server.user.DefaultUserModelInitializer;

/**
 * Parses configuration data for the SiteWhere datastore section.
 * 
 * @author Derek
 */
public class DatastoreParser extends SiteWhereBeanDefinitionParser {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
     * parseInternal (org.w3c.dom.Element,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
	List<Element> dsChildren = DomUtils.getChildElements(element);
	for (Element child : dsChildren) {
	    if (!IConfigurationElements.SITEWHERE_COMMUNITY_NS.equals(child.getNamespaceURI())) {
		NamespaceHandler nested = context.getReaderContext().getNamespaceHandlerResolver()
			.resolve(child.getNamespaceURI());
		if (nested != null) {
		    nested.parse(child, context);
		    continue;
		} else {
		    throw new RuntimeException(
			    "Invalid nested element found in 'datastore' section: " + child.toString());
		}
	    }
	    Elements type = Elements.getByLocalName(child.getLocalName());
	    if (type == null) {
		throw new RuntimeException("Unknown datastore element: " + child.getLocalName());
	    }
	    switch (type) {
	    case Mongo: {
		parseMongoDatasource(child, context);
		break;
	    }
	    case HBase: {
		parseHBaseDatasource(child, context);
		break;
	    }
	    case DefaultUserModelInitializer: {
		parseDefaultUserModelInitializer(child, context);
		break;
	    }
	    case GroovyUserModelInitializer: {
		parseGroovyUserModelInitializer(child, context);
		break;
	    }
	    case DefaultTenantModelInitializer: {
		parseDefaultTenantModelInitializer(child, context);
		break;
	    }
	    case GroovyTenantModelInitializer: {
		parseGroovyTenantModelInitializer(child, context);
		break;
	    }
	    }
	}
	return null;
    }

    /**
     * Parse a MongoDB datasource configuration and create beans needed to
     * realize it.
     * 
     * @param element
     * @param context
     */
    protected void parseMongoDatasource(Element element, ParserContext context) {
	boolean docker = false;
	Attr useDockerLinking = element.getAttributeNode("useDockerLinking");
	if ((useDockerLinking != null) && ("true".equals(useDockerLinking.getValue()))) {
	    docker = true;
	}

	// Register client bean.
	BeanDefinitionBuilder client = docker ? getBuilderFor(DockerMongoClient.class)
		: getBuilderFor(SiteWhereMongoClient.class);
	parseMongoAttributes(element, context, client);
	context.getRegistry().registerBeanDefinition("mongo", client.getBeanDefinition());

	// Register Mongo user management implementation.
	BeanDefinitionBuilder um = BeanDefinitionBuilder.rootBeanDefinition(MongoUserManagement.class);
	um.addPropertyReference("mongoClient", "mongo");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_USER_MANAGEMENT, um.getBeanDefinition());

	// Register Mongo tenant management implementation.
	BeanDefinitionBuilder tm = BeanDefinitionBuilder.rootBeanDefinition(MongoTenantManagement.class);
	tm.addPropertyReference("mongoClient", "mongo");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_TENANT_MANAGEMENT,
		tm.getBeanDefinition());
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
    }

    /**
     * Parse an HBase datasource configuration and create beans needed to
     * realize it.
     * 
     * @param element
     * @param context
     */
    protected void parseHBaseDatasource(Element element, ParserContext context) {
	// Register client bean.
	BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(DefaultHBaseClient.class);
	parseHBaseAttributes(element, context, client);
	context.getRegistry().registerBeanDefinition("hbase", client.getBeanDefinition());

	// Register HBase user management implementation.
	BeanDefinitionBuilder um = BeanDefinitionBuilder.rootBeanDefinition(HBaseUserManagement.class);
	um.addPropertyReference("client", "hbase");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_USER_MANAGEMENT, um.getBeanDefinition());

	// Register HBase tenant management implementation.
	BeanDefinitionBuilder tm = BeanDefinitionBuilder.rootBeanDefinition(HBaseTenantManagement.class);
	tm.addPropertyReference("client", "hbase");
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_TENANT_MANAGEMENT,
		tm.getBeanDefinition());
    }

    /**
     * Common parser logic for HBase attributes.
     * 
     * @param element
     * @param context
     * @param client
     */
    public static void parseHBaseAttributes(Element element, ParserContext context, BeanDefinitionBuilder client) {
	Attr quorum = element.getAttributeNode("quorum");
	if (quorum != null) {
	    client.addPropertyValue("quorum", quorum.getValue());
	}

	Attr zookeeperClientPort = element.getAttributeNode("zookeeperClientPort");
	if (zookeeperClientPort != null) {
	    client.addPropertyValue("zookeeperClientPort", zookeeperClientPort.getValue());
	}

	Attr zookeeperZnodeParent = element.getAttributeNode("zookeeperZnodeParent");
	if (zookeeperZnodeParent != null) {
	    client.addPropertyValue("zookeeperZnodeParent", zookeeperZnodeParent.getValue());
	}

	Attr zookeeperZnodeRootServer = element.getAttributeNode("zookeeperZnodeRootServer");
	if (zookeeperZnodeRootServer != null) {
	    client.addPropertyValue("zookeeperZnodeRootServer", zookeeperZnodeRootServer.getValue());
	}
    }

    /**
     * Parse configuration for default asset model initializer.
     * 
     * @param element
     * @param context
     */
    protected void parseDefaultUserModelInitializer(Element element, ParserContext context) {
	BeanDefinitionBuilder uinit = BeanDefinitionBuilder.rootBeanDefinition(DefaultUserModelInitializer.class);
	Attr initializeIfNoConsole = element.getAttributeNode("initializeIfNoConsole");
	if ((initializeIfNoConsole == null) || ("true".equals(initializeIfNoConsole.getValue()))) {
	    uinit.addPropertyValue("initializeIfNoConsole", "true");
	}
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_USER_MODEL_INITIALIZER,
		uinit.getBeanDefinition());
    }

    /**
     * Parse configuration for Groovy user model initializer.
     * 
     * @param element
     * @param context
     */
    protected void parseGroovyUserModelInitializer(Element element, ParserContext context) {
	BeanDefinitionBuilder init = BeanDefinitionBuilder.rootBeanDefinition(GroovyUserModelInitializer.class);

	Attr scriptPath = element.getAttributeNode("scriptPath");
	if (scriptPath != null) {
	    init.addPropertyValue("scriptPath", scriptPath.getValue());
	}

	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_USER_MODEL_INITIALIZER,
		init.getBeanDefinition());
    }

    /**
     * Parse configuration for default asset model initializer.
     * 
     * @param element
     * @param context
     */
    protected void parseDefaultTenantModelInitializer(Element element, ParserContext context) {
	BeanDefinitionBuilder tinit = BeanDefinitionBuilder.rootBeanDefinition(DefaultTenantModelInitializer.class);
	Attr initializeIfNoConsole = element.getAttributeNode("initializeIfNoConsole");
	if ((initializeIfNoConsole == null) || ("true".equals(initializeIfNoConsole.getValue()))) {
	    tinit.addPropertyValue("initializeIfNoConsole", "true");
	}
	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_TENANT_MODEL_INITIALIZER,
		tinit.getBeanDefinition());
    }

    /**
     * Parse configuration for Groovy user model initializer.
     * 
     * @param element
     * @param context
     */
    protected void parseGroovyTenantModelInitializer(Element element, ParserContext context) {
	BeanDefinitionBuilder init = BeanDefinitionBuilder.rootBeanDefinition(GroovyTenantModelInitializer.class);

	Attr scriptPath = element.getAttributeNode("scriptPath");
	if (scriptPath != null) {
	    init.addPropertyValue("scriptPath", scriptPath.getValue());
	}

	context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_TENANT_MODEL_INITIALIZER,
		init.getBeanDefinition());
    }

    /**
     * Expected child elements.
     * 
     * @author Derek
     */
    public static enum Elements {

	/** Mongo datastore and service providers */
	Mongo("mongo-datastore"),

	/** HBase datastore and service providers */
	HBase("hbase-datastore"),

	/** Creates sample data if no user data is present */
	DefaultUserModelInitializer("default-user-model-initializer"),

	/** Uses Groovy script to create user data */
	GroovyUserModelInitializer("groovy-user-model-initializer"),

	/** Creates sample data if no tenant data is present */
	DefaultTenantModelInitializer("default-tenant-model-initializer"),

	/** Uses Groovy script to create tenant data */
	GroovyTenantModelInitializer("groovy-tenant-model-initializer");

	/** Event code */
	private String localName;

	private Elements(String localName) {
	    this.localName = localName;
	}

	public static Elements getByLocalName(String localName) {
	    for (Elements value : Elements.values()) {
		if (value.getLocalName().equals(localName)) {
		    return value;
		}
	    }
	    return null;
	}

	public String getLocalName() {
	    return localName;
	}

	public void setLocalName(String localName) {
	    this.localName = localName;
	}
    }
}