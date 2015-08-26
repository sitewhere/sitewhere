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
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.sitewhere.hbase.DefaultHBaseClient;
import com.sitewhere.hbase.user.HBaseUserManagement;
import com.sitewhere.mongodb.DockerMongoClient;
import com.sitewhere.mongodb.SiteWhereMongoClient;
import com.sitewhere.mongodb.device.MongoDeviceManagement;
import com.sitewhere.mongodb.user.MongoUserManagement;
import com.sitewhere.server.SiteWhereServerBeans;
import com.sitewhere.server.user.DefaultUserModelInitializer;

/**
 * Parses information for the global datastore configuration.
 * 
 * @author Derek
 */
public class GlobalDatastoreParser extends AbstractBeanDefinitionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#parseInternal
	 * (org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		List<Element> dsChildren = DomUtils.getChildElements(element);
		for (Element child : dsChildren) {
			if (!IConfigurationElements.SITEWHERE_COMMUNITY_NS.equals(child.getNamespaceURI())) {
				NamespaceHandler nested =
						context.getReaderContext().getNamespaceHandlerResolver().resolve(
								child.getNamespaceURI());
				if (nested != null) {
					nested.parse(child, context);
					continue;
				} else {
					throw new RuntimeException("Invalid nested element found in 'datastore' section: "
							+ child.toString());
				}
			}
			Elements type = Elements.getByLocalName(child.getLocalName());
			if (type == null) {
				throw new RuntimeException("Unknown datastore element: " + child.getLocalName());
			}
			switch (type) {
			case MongoGlobal: {
				parseMongoGlobalDatasource(child, context);
				break;
			}
			case HBaseGlobal: {
				parseHBaseGlobalDatasource(child, context);
				break;
			}
			case DefaultUserModelInitializer: {
				parseDefaultUserModelInitializer(child, context);
				break;
			}
			}
		}
		return null;
	}

	/**
	 * Parse a MongoDB datasource configuration and create beans needed to realize it.
	 * 
	 * @param element
	 * @param context
	 */
	protected void parseMongoGlobalDatasource(Element element, ParserContext context) {
		boolean docker = false;
		Attr useDockerLinking = element.getAttributeNode("useDockerLinking");
		if ((useDockerLinking != null) && ("true".equals(useDockerLinking.getValue()))) {
			docker = true;
		}

		// Register client bean.
		BeanDefinitionBuilder client =
				docker ? BeanDefinitionBuilder.rootBeanDefinition(DockerMongoClient.class)
						: BeanDefinitionBuilder.rootBeanDefinition(SiteWhereMongoClient.class);
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
		context.getRegistry().registerBeanDefinition("mongo", client.getBeanDefinition());

		// Determine if username and password are supplied.
		Attr username = element.getAttributeNode("username");
		Attr password = element.getAttributeNode("password");
		if ((username != null) && ((password == null))) {
			throw new RuntimeException(
					"If username is specified for MongoDB, password must be specified as well.");
		}
		if ((username == null) && ((password != null))) {
			throw new RuntimeException(
					"If password is specified for MongoDB, username must be specified as well.");
		}
		if ((username != null) && (password != null)) {
			client.addPropertyValue("username", username.getValue());
			client.addPropertyValue("password", password.getValue());
		}

		// Register Mongo device management implementation.
		BeanDefinitionBuilder dm = BeanDefinitionBuilder.rootBeanDefinition(MongoDeviceManagement.class);
		dm.addPropertyReference("mongoClient", "mongo");

		Attr useBulkEventInserts = element.getAttributeNode("useBulkEventInserts");
		if (useBulkEventInserts != null) {
			dm.addPropertyValue("useBulkEventInserts", useBulkEventInserts.getValue());
		}

		Attr bulkInsertMaxChunkSize = element.getAttributeNode("bulkInsertMaxChunkSize");
		if (bulkInsertMaxChunkSize != null) {
			dm.addPropertyValue("bulkInsertMaxChunkSize", bulkInsertMaxChunkSize.getValue());
		}

		// Register Mongo user management implementation.
		BeanDefinitionBuilder um = BeanDefinitionBuilder.rootBeanDefinition(MongoUserManagement.class);
		um.addPropertyReference("mongoClient", "mongo");
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_USER_MANAGEMENT,
				um.getBeanDefinition());
	}

	/**
	 * Parse an HBase datasource configuration and create beans needed to realize it.
	 * 
	 * @param element
	 * @param context
	 */
	protected void parseHBaseGlobalDatasource(Element element, ParserContext context) {
		// Register client bean.
		BeanDefinitionBuilder client = BeanDefinitionBuilder.rootBeanDefinition(DefaultHBaseClient.class);
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

		context.getRegistry().registerBeanDefinition("hbase", client.getBeanDefinition());

		// Register HBase user management implementation.
		BeanDefinitionBuilder um = BeanDefinitionBuilder.rootBeanDefinition(HBaseUserManagement.class);
		um.addPropertyReference("client", "hbase");
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_USER_MANAGEMENT,
				um.getBeanDefinition());
	}

	/**
	 * Parse configuration for default user model initializer.
	 * 
	 * @param element
	 * @param context
	 */
	protected void parseDefaultUserModelInitializer(Element element, ParserContext context) {
		BeanDefinitionBuilder init =
				BeanDefinitionBuilder.rootBeanDefinition(DefaultUserModelInitializer.class);
		Attr initializeIfNoConsole = element.getAttributeNode("initializeIfNoConsole");
		if ((initializeIfNoConsole == null) || ("true".equals(initializeIfNoConsole.getValue()))) {
			init.addPropertyValue("initializeIfNoConsole", "true");
		}
		context.getRegistry().registerBeanDefinition(SiteWhereServerBeans.BEAN_USER_MODEL_INITIALIZER,
				init.getBeanDefinition());
	}

	/**
	 * Expected child elements.
	 * 
	 * @author Derek
	 */
	public static enum Elements {

		/** Mongo datastore and service providers */
		MongoGlobal("mongo-global-datastore"),

		/** HBase datastore and service providers */
		HBaseGlobal("hbase-global-datastore"),

		/** Creates sample data if no device data is present */
		DefaultUserModelInitializer("default-user-model-initializer");

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